package it.polimi.ingsw.Network;

import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.*;
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
import java.util.logging.*;

public class ServerImplementation extends UnicastRemoteObject implements Server {
    private static ServerImplementation instance;
    public static final Logger logger = Logger.getLogger("ServerImplementation");
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private List<String> playingUsernames; // to disconnect
    private Map<String, GameServer> disconnectedUsernames;
    private Queue<GameServer> lobbiesWaitingToStart;

    private ServerImplementation() throws RemoteException {
        super();
        setUpLogger();
        playingUsernames = new ArrayList<>();
        disconnectedUsernames = new HashMap<>();
        lobbiesWaitingToStart = new LinkedBlockingQueue<>();
    }

    // it has to handle only the message for connect the client to his GameServer
    // and to reconnect a client to his GameServer
    public void handleMessage(Message m, Client client) throws RemoteException {
        if( m instanceof RegisterMessage ) {
            register( ((RegisterMessage) m).getUsername(), ((RegisterMessage) m).getNumOfPlayers(), ( (message) -> {
                try {
                    client.update(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }) );
        }
        else if( m instanceof ReconnectMessage) {
            reconnect( ((ReconnectMessage) m).getUsername(), ( (message) -> {
                try {
                    client.update(message);
                } catch (RemoteException e) {
                    System.err.println("Cannot send message to client");
                }
            }) );
        }
        else System.err.println("Message not recognized; ignoring it");
    }

    public void deleteGame(List<String> players) {
        logger.log(Level.INFO, "Deleting game");
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
        logger.log(Level.INFO, "Player " + username + " disconnected");
        synchronized (playingUsernames) {
            if( !playingUsernames.contains(username) ) return;
            playingUsernames.remove(username);
        }
        synchronized (disconnectedUsernames) {
            disconnectedUsernames.put(username, gameServer);
        }
    }

    public void kick(String username, GameServer lobby) {
        logger.log(Level.INFO, "Player " + username + " kicked");

        synchronized (playingUsernames) {
            if( lobby == lobbiesWaitingToStart.peek() && lobby.getNumOfActivePlayers() == 0 ) {
                lobbiesWaitingToStart.poll();
                while(lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbiesWaitingToStart.poll();
            }
        }
    }

    private void reconnect(String username, GameListener listener) {
        logger.log(Level.INFO, "Reconnect request for " + username );
        synchronized (playingUsernames) {
            synchronized (disconnectedUsernames) {
                if( playingUsernames.contains(username) ) {
                    logger.log(Level.INFO, "A player with username:  " + username + " is already loggedin");
                    listener.update(new TakenUsernameMessage());
                    return;
                }
                else if( !(disconnectedUsernames.containsKey(username)) ) {
                    logger.log(Level.INFO, "No player ever played with username:  " + username);
                    listener.update(new NoUsernameToReconnectMessage());
                    return;
                }
                logger.log(Level.INFO, "Reconnecting player with username:  " + username);

                playingUsernames.add(username);
                disconnectedUsernames.get(username).reconnect(username, listener);
                listener.update(new GameServerMessage(disconnectedUsernames.get(username)));
                disconnectedUsernames.remove(username);
            }
        }
    }

    //numOfPlayers is 1 when the player wants to join a lobby, otherwise is the numOfPlayers for the new lobby
    private void register(String username, int numOfPlayers, GameListener listener) throws RemoteException {
        logger.log(Level.INFO, "Register request for player with username:  " + username + " and numOfPlayers: " + numOfPlayers);

        if( ( numOfPlayers <= 0 ) || ( numOfPlayers>4 ) ) {
            listener.update(new InvalidNumOfPlayersMessage());
            return;
        }
        if( username == null ) {
            listener.update(new MissingUsernameMessage());
            return;
        }
        logger.log(Level.INFO, "Register request for player with username:  " + username + " parameters were valid. Loggin player in");

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
                logger.log(Level.INFO, "In attesa di un client via socket...");
                Socket socket = serverSocket.accept();
                System.out.println("Socket Client connected");
                logger.log(Level.INFO, "Nuova connessione via socket accettata");

                instance.executorService.submit(() -> {
                    try {
                        ClientSkeleton clientSkeleton = new ClientSkeleton(server, socket);
                        while(true) {
                            clientSkeleton.receive();
                        }
                    } catch (RemoteException e) {
                        System.err.println("Cannot receive from client. Closing this connection...");
                        logger.log(Level.SEVERE, "Errore nella ricezione da client");

                    } finally {
                        System.out.println("Client disconnected");
                        logger.log(Level.INFO, "Client socket disconnesso");

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
                logger.log(Level.INFO, "Avvio servizio RMI");
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
                logger.log(Level.INFO, "Avvio servizio Socket");

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
        logger.log(Level.INFO, "Server avviato");

    }

    private static void setUpLogger(){
        logger.setLevel(Level.ALL); // Imposta il livello di logging desiderato

        FileHandler fileHandler;

        // Crea un gestore di log su file
        try{
            fileHandler = new FileHandler("server.log");
        }catch (IOException ex){
            System.err.println("Cannot create log file. Halting...");
            return;
        }
        fileHandler.setFormatter(new SimpleFormatter());

        // Crea un gestore di log sulla console
        ConsoleHandler consoleHandler = new ConsoleHandler();

        // Imposta il livello di logging dei gestori
        fileHandler.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.ALL);

        // Aggiungi i gestori al logger
        logger.addHandler(fileHandler);
        //logger.addHandler(consoleHandler);

    }

}
