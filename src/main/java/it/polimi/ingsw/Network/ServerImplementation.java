package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Network.Middleware.ClientSkeleton;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerImplementation extends UnicastRemoteObject implements Server {

    private static ServerImplementation instance;
    private final ExecutorService executorService = Executors.newCachedThreadPool();;
    private Map<String, Controller> playingUsernames;
    private Map<String, Controller> disconnectedUsernames;
    private Queue<Controller> lobbiesWaitingToStart;

    protected ServerImplementation() throws RemoteException {
    }

    //numOfPlayers is 1 when the player wants to join a lobby, otherwise is the numOfPlayers for the new lobby
    private void register(String username, int numOfPlayers, GameListener listener){
        if(numOfPlayers<=0 || numOfPlayers>4){
            listener.update(new InvalidNumOfPlayersMessage());
            return;
        }
        if(username == null){
            listener.update(new MissingUsernameMessage());
            return;
        }
        synchronized (playingUsernames){
            synchronized (disconnectedUsernames){
                if(playingUsernames.containsKey(username) || disconnectedUsernames.containsKey(username)){
                    listener.update(new TakenUsernameMessage());
                    return;
                }
            }
            synchronized (lobbiesWaitingToStart){
                if(numOfPlayers != 1){
                    Controller lobby = new Controller(numOfPlayers,this);
                    lobby.addPlayer(username,listener);
                    playingUsernames.put(username,lobby);
                        lobbiesWaitingToStart.add(lobby);
                }
                else{
                    if(lobbiesWaitingToStart.peek() != null){
                        if(lobbiesWaitingToStart.peek().addPlayer(username,listener)) {
                            lobbiesWaitingToStart.poll();
                            while(lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbiesWaitingToStart.poll();
                        }
                    }
                    else listener.update(new NoLobbyAvailableMessage());
                }
            }
        }
    }

    private void reconnect(String username, GameListener listener){
        synchronized (playingUsernames){
            synchronized (disconnectedUsernames){
                if( !(disconnectedUsernames.containsKey(username)) ){

                    listener.update(new NoUsernameToReconnectMessage());

                    return;
                }
                playingUsernames.put(username,disconnectedUsernames.get(username));
                disconnectedUsernames.remove(username);
                playingUsernames.get(username).reconnect(username,listener);
            }
        }
    }

    // when Client does not ping back and when the user quits the lobby or the game
    private void disconnect(String username){
        synchronized (playingUsernames){
            if( !playingUsernames.containsKey(username) ) return;
            Controller controller = playingUsernames.get(username);
            playingUsernames.remove(username);
            if(controller.isGameStarted()){
                synchronized (disconnectedUsernames){
                    disconnectedUsernames.put(username,controller);
                    controller.disconnect(username);
                }
            }
            else{
                controller.kick(username);
                if(controller == lobbiesWaitingToStart.peek() && controller.getNumOfActivePlayers() == 0){
                    lobbiesWaitingToStart.poll();
                    while(lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbiesWaitingToStart.poll();
                }
            }
        }
    }

    private void doTurn(String username, Coordinates[] chosenTiles, int column){
        synchronized (playingUsernames){
            if( !playingUsernames.containsKey(username) || !(playingUsernames.get(username).isGameStarted()) ) return; // message is ignored
            Controller controller = playingUsernames.get(username);
            controller.doTurn(username,chosenTiles,column);
        }
    }

    public void handleMessage( Message m ){


        if( m instanceof ReconnectMessage){
            reconnect(((ReconnectMessage) m).getUsername(), (message -> {
                try {
                    ((ReconnectMessage)m).getClient().update(message);
                } catch (RemoteException e) {
                    System.err.println("Cannot send message to client");
                }
            }));
        }
        else if(m instanceof RegisterMessage){
            register(((RegisterMessage) m).getUsername(),((RegisterMessage) m).getNumOfPlayers(), (message -> {
                try {
                    ((RegisterMessage)m).getClient().update(message);
                } catch (RemoteException e) {
                    System.err.println("Cannot send message to client");
                }
            }));
        }
        else if(m instanceof TurnActionMessage){

            TurnActionMessage message = (TurnActionMessage) m;
            doTurn(message.getUsername(),message.getChosenTiles(),message.getColumn());

        }
        else if(m instanceof DisconnectMessage){
            DisconnectMessage message = (DisconnectMessage) m;
            disconnect(message.getUsername());
        }
        else{
            System.err.println("Message not recognized; ignoring it");
        }


    }

    private void startRMI() throws RemoteException {
        ServerImplementation server = getInstance();

        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("G26-MyShelfie-Server", server);
    }

    public static void startSocket() throws RemoteException {
        ServerImplementation instance = getInstance();
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            while (true) {
                Socket socket = serverSocket.accept();
                instance.executorService.submit(() -> {
                    try {
                        ClientSkeleton clientSkeleton = new ClientSkeleton(socket);

                        while (true) {
                            clientSkeleton.receive(instance);
                        }
                    } catch (RemoteException e) {
                        System.err.println("Cannot receive from client. Closing this connection...");
                    } finally {
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
        if (instance == null) {
            instance = new ServerImplementation();
        }
        return instance;
    }

    public void deleteGame(List<String> players){
        synchronized (playingUsernames){
            synchronized (disconnectedUsernames){
                for(String player: players){
                    playingUsernames.remove(player);
                    disconnectedUsernames.remove(player);
                }
            }
        }
    }


}
