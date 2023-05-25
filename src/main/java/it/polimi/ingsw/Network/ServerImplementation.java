package it.polimi.ingsw.Network;

import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Utilities.Config;
import it.polimi.ingsw.Network.Middleware.ClientSkeleton;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class ServerImplementation extends UnicastRemoteObject implements Server {
    private static ServerImplementation instance;
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private List<String> playingUsernames; // to disconnect
    private Map<String, GameServer> disconnectedUsernames;
    private Queue<GameServer> lobbiesWaitingToStart;

    private ServerImplementation() throws RemoteException {
        playingUsernames = new ArrayList<>();
        disconnectedUsernames = new HashMap<>();
        lobbiesWaitingToStart = new LinkedBlockingQueue<>();
    }

    // it has to handle only the message for connect the client to his GameServer
    // and to reconnect a client to his GameServer
    public void handleMessage(Message m) throws RemoteException {
        if( m instanceof RegisterMessage ) {
            register( ((RegisterMessage) m).getUsername(), ((RegisterMessage) m).getNumOfPlayers(), ( (message) -> {
                try {
                    ((RegisterMessage)m).getClient().update(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }) );
        }
        else if( m instanceof ReconnectMessage) {
            reconnect( ((ReconnectMessage) m).getUsername(), ( (message) -> {
                try {
                    ((ReconnectMessage)m).getClient().update(message);
                } catch (RemoteException e) {
                    System.err.println("Cannot send message to client");
                }
            }) );
        }
        else System.err.println("Message not recognized; ignoring it");
    }

    public void deleteGame(List<String> players) {
        synchronized (playingUsernames) {
            synchronized (disconnectedUsernames) {
                for( String player : players ) {
                    playingUsernames.remove(player);
                    disconnectedUsernames.remove(player);
                }
            }
        }
    }

    public void disconnect(String username, GameServer gameServer) {
        synchronized (playingUsernames) {
            if( !playingUsernames.contains(username) ) return;
            playingUsernames.remove(username);
        }
        synchronized (disconnectedUsernames) {
            disconnectedUsernames.put(username, gameServer);
        }
    }

    public void kick(String username, GameServer lobby) {
        synchronized (playingUsernames) {
            if( lobby == lobbiesWaitingToStart.peek() && lobby.getNumOfActivePlayers() == 0 ) {
                lobbiesWaitingToStart.poll();
                while(lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbiesWaitingToStart.poll();
            }
        }
    }

    private void reconnect(String username, GameListener listener) {
        System.out.println("Reconnecting request for " + username);
        synchronized (playingUsernames) {
            synchronized (disconnectedUsernames) {
                if( playingUsernames.contains(username) ) {
                    listener.update(new TakenUsernameMessage());
                    return;
                }
                else if( !(disconnectedUsernames.containsKey(username)) ) {
                    listener.update(new NoUsernameToReconnectMessage());
                    return;
                }
                playingUsernames.add(username);
                disconnectedUsernames.get(username).reconnect(username, listener);
                listener.update(new GameServerMessage(disconnectedUsernames.get(username)));
                disconnectedUsernames.remove(username);
            }
        }
    }

    //numOfPlayers is 1 when the player wants to join a lobby, otherwise is the numOfPlayers for the new lobby
    private void register(String username, int numOfPlayers, GameListener listener) throws RemoteException {
        if( ( numOfPlayers <= 0 ) || ( numOfPlayers > Config.getInstance().getMaxNumberOfPlayers()) ) {
            listener.update(new InvalidNumOfPlayersMessage());
            return;
        }
        if( username == null ) {
            listener.update(new MissingUsernameMessage());
            return;
        }
        synchronized (playingUsernames) {
            synchronized (disconnectedUsernames) {
                if( playingUsernames.contains(username) || disconnectedUsernames.containsKey(username) ) {
                    listener.update(new TakenUsernameMessage());
                    return;
                }
            }
            synchronized (lobbiesWaitingToStart){
                if( numOfPlayers != 1 ){
                    GameServer lobby = new GameServer(this,numOfPlayers);
                    lobby.addPlayer(username, listener);
                    playingUsernames.add(username);
                    lobbiesWaitingToStart.add(lobby);
                    listener.update(new GameServerMessage(lobby));
                }
                else {
                    if( lobbiesWaitingToStart.peek() != null ) {
                        GameServer lobby = lobbiesWaitingToStart.peek();
                        if( lobby.addPlayer(username, listener) ) { // full
                            lobbiesWaitingToStart.poll();
                            while( lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0 )
                                lobbiesWaitingToStart.poll();
                        }
                        listener.update(new GameServerMessage(lobby));
                    }
                    else listener.update(new NoLobbyAvailableMessage());
                }
            }
        }
    }

    private static void startRMI(Server server) throws RemoteException {
        LocateRegistry.createRegistry(1099);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("G26-MyShelfie-Server", server);
        System.out.println("In attesa di client RMI...");
    }

    public static void startSocket(Server server) throws RemoteException {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            while(true) {
                System.out.println("In attesa di un client via socket...");
                Socket socket = serverSocket.accept();
                System.out.println("Socket Client connected");
                instance.executorService.submit(() -> {
                    try {
                        ClientSkeleton clientSkeleton = new ClientSkeleton(server, socket);
                        while(true) {
                            clientSkeleton.receive();
                        }
                    } catch (RemoteException e) {
                        System.err.println("Cannot receive from client. Closing this connection...");
                    } finally {
                        System.out.println("Client disconnected");
                        try {
                            socket.close();
                        } catch (IOException e) {
                            System.err.println("Cannot close socket");
                        }
                    }
                });
            }
        } catch (IOException e) {
            throw new RemoteException("Cannot start socket server", e);
        }
    }

    // Return the singleton instance of the server
    public static ServerImplementation getInstance() throws RemoteException{
        if( instance == null ) {
            instance = new ServerImplementation();
        }
        return instance;
    }

    public static void main(String[] args){
        ServerImplementation server = null;

        try {
            server = getInstance();
        } catch (RemoteException e) {
            System.err.println("Cannot get server");
            System.err.println(e.getMessage());
            return;
        }

        Thread rmiThread = new Thread() {
            @Override
            public void run() {
                try {
                    startRMI(getInstance());
                } catch (RemoteException e) {
                    System.err.println("Cannot start RMI. This protocol will be disabled.");
                }
            }
        };

        rmiThread.start();

        Thread socketThread = new Thread() {
            @Override
            public void run() {
                try {
                    startSocket(getInstance());
                } catch (RemoteException e) {
                    System.err.println("Cannot start RMI server");
                    System.err.println(e.getMessage());
                }
            }
        };

        socketThread.start();

        System.out.println("Server started");
    }

}
