package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Tuple;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The `GameServer` class represents the server component responsible for managing the game lobby and the game itself.
 * It acts as a lobby before the game starts, allowing players to join and wait for the game to begin. Once the game starts,
 * it serves as the server for the ongoing game, handling player actions and managing the game state.
 *
 * The `GameServer` implements the `Server` interface, which defines the communication protocol between the server and clients.
 * It extends the `UnicastRemoteObject` class to enable remote method invocation (RMI) functionality.
 *
 * The `GameServer` maintains a reference to the `Controller` responsible for managing the game logic and state. It also holds
 * references to the `ServerImplementation` and acts as a mediator between the game and the server.
 *
 * The `GameServer` keeps track of the players in the lobby using the `playingUsernames` list and the disconnected players using
 * the `disconnectedUsernames` list. The `playingUsernames` list contains the usernames of players who are currently connected
 * and actively participating in the game. The `disconnectedUsernames` map stores the usernames of players who were previously
 * connected but got disconnected. The map holds the username as the key and the corresponding `GameServer` object as the value,
 * allowing for easy reconnection of players.
 *
 * The `GameServer` class provides methods to handle incoming messages from clients. It distinguishes between turn action messages
 * and disconnect messages. For turn action messages, it calls the `doTurn` method to process the player's turn. For disconnect
 * messages, it triggers the `disconnect` method to handle the player's disconnection.
 *
 * The class also provides methods to check if the game has started, delete the game and remove players from the game, reconnect
 * disconnected players, add new players to the lobby, and retrieve the number of active players in the game.
 *
 * Note: The `GameServer` class is meant to be used in a distributed environment and supports both RMI and socket communication
 * protocols. It acts as a server for RMI-based clients and uses the `ClientSkeleton` class to handle socket-based clients.
 * The `ServerImplementation` class is responsible for managing the server-side logic and communication protocols.
 */
public class GameServer extends UnicastRemoteObject implements Server {
    private Controller controller;
    private ServerImplementation serverImplementation = null;
    private List<String> playingUsernames;
    private List<String> disconnectedUsernames;
    private Queue<Tuple<Message, Client>> recievedMessages = new LinkedList<>();
    private int nextIdPing = 0;
    private int[] idPingToBeAnswered;
    private Timer[] playersTimers;
    private TimerTask[] playersTimersTasks;


    /**
     * Constructs a GameServer instance with the specified ServerImplementation and number of players.
     *
     * @param serverImplementation the server implementation handling game-related operations
     * @param numOfPlayers         the number of players in the game
     * @throws RemoteException if a remote communication error occurs
     */
    public GameServer(ServerImplementation serverImplementation, int numOfPlayers) throws RemoteException {
        super();
        this.serverImplementation = ServerImplementation.getInstance();
        this.controller = new Controller(new Game(numOfPlayers), this);
        this.playingUsernames = new ArrayList<>();
        this.disconnectedUsernames = new ArrayList<>();

        new Thread(){
            @Override
            public void run(){

                while(true){

                    if( isMessagesQueueEmpty() ) continue;

                    Tuple<Message,Client> tuple = popFromMessagesQueue();
                    try {
                        effectivelyHandlMessage(tuple.getFirst(), tuple.getSecond());
                    }catch (RemoteException ex){
                        ServerImplementation.logger.log(Level.SEVERE, "Cannot send message to client: " + ex.getMessage());
                    }
                }
            }

        }.start();

        idPingToBeAnswered = new int[numOfPlayers];
        for(int i = 0;i<idPingToBeAnswered.length;i++){
            idPingToBeAnswered[i] = -1;
        }
        playersTimers = new Timer[numOfPlayers];
        playersTimersTasks= new TimerTask[numOfPlayers];

        new Thread(){
            @Override
            public void run() {
                while(true){
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ServerImplementation.logger.log(Level.INFO, "NEW ROUND OF PINGS");
                    synchronized (playingUsernames){
                        for(int index = 0;index<playingUsernames.size();index++){

                            GameListener listener = controller.getListener(index);
                            if(listener == null || playingUsernames.get(index) == null) continue;
                            final String usernameForThread = playingUsernames.get(index);


                            PingMessage pingMessage = new PingMessage(nextIdPing);
                            idPingToBeAnswered[index] = nextIdPing;
                            ServerImplementation.logger.log(Level.INFO,"Set ping #" + pingMessage.getpingNumber() + " for player " + usernameForThread + "(index: " + index + ")");

                            nextIdPing = (nextIdPing+1) % 100000;

                            playersTimers[index] = new Timer();

                            final int indexThread = index;
                            playersTimersTasks[index] = new TimerTask() {
                                @Override
                                public void run() {
                                    synchronized (playingUsernames){
                                        //player has not answered to ping
                                        //disconnect player
                                        ServerImplementation.logger.log(Level.INFO, usernameForThread + " has not answered to ping #" + idPingToBeAnswered[indexThread] + " in time; DISCONNECTING HIM");
                                        disconnect(usernameForThread);


                                    }
                                }
                            };

                            playersTimers[index].schedule(playersTimersTasks[index], 7500);

                            try{
                                listener.update(pingMessage);
                            }catch (Exception ex){
                                ServerImplementation.logger.log(Level.SEVERE, "Cannot send ping message to client: " + ex.getMessage());
                                continue;
                            }

                            ServerImplementation.logger.log(Level.INFO,"Sent ping #" + pingMessage.getpingNumber() + " to " + usernameForThread);


                        }
                    }
                }
            }
        }.start();
    }

    public void resetTimerAndPing(PingMessage message){
        //get index of element in idPingToBeAnswered with value message.getpingNumber()
        ServerImplementation.logger.log(Level.INFO,"Startd to handle ping #" + message.getpingNumber());
        synchronized (playingUsernames){
            int index = -1;
            for(int i = 0; i < idPingToBeAnswered.length; i++){
                boolean resCheck = idPingToBeAnswered[i] == message.getpingNumber();
                if(resCheck){
                    index = i;
                    ServerImplementation.logger.log(Level.INFO,"Ping of player " + playingUsernames.get(i) + " at index i=" + i);
                    break;
                }
            }

            if(index == -1) return;


            if(playersTimers[index] != null){
                playersTimersTasks[index].cancel();
                playersTimers[index].cancel();
            }

            ServerImplementation.logger.log(Level.INFO,"Reset timer and ping for player " + playingUsernames.get(index) + " at index i="+index);

        }

    }



    /**
     * Adds a message and its associated client to the message queue.
     *
     * @param m The message to be added to the queue.
     * @param c The client associated with the message.
     */
    private void addToMessagesQueue(Message m, Client c) {
        synchronized (recievedMessages) {
            recievedMessages.add(new Tuple<>(m, c));
        }
    }

    /**
     * Checks if the message queue is empty.
     *
     * @return {@code true} if the message queue is empty, {@code false} otherwise.
     */
    private boolean isMessagesQueueEmpty() {
        synchronized (recievedMessages) {
            return recievedMessages.isEmpty();
        }
    }

    /**
     * Removes and returns a message from the message queue.
     *
     * @return The message and associated client as a tuple, or {@code null} if the queue is empty.
     */
    private Tuple<Message, Client> popFromMessagesQueue() {
        synchronized (recievedMessages) {
            return recievedMessages.poll();
        }
    }

    /**
     * Handles a received message and performs appropriate actions based on its type.
     * This method is called to process messages received by the server.
     *
     * @param m      The message to be effectively handled.
     * @param client The client associated with the message.
     * @throws RemoteException If a remote exception occurs during message handling.
     */
    private void effectivelyHandlMessage(Message m, Client client) throws RemoteException {
        if( m instanceof TurnActionMessage ) {
            TurnActionMessage message = (TurnActionMessage) m;
            ServerImplementation.logger.log(Level.INFO, "Started to handle TurnMessage");
            doTurn(message.getUsername(),message.getChosenTiles(),message.getColumn());
        }
        else if( m instanceof DisconnectMessage ) {
            DisconnectMessage message = (DisconnectMessage) m;
            ServerImplementation.logger.log(Level.INFO, "Started to handle DisconnectMessage");

            disconnect(message.getUsername());
        }
        else if(m instanceof PingMessage){
            //TODO: HANDLE PING ACK
            resetTimerAndPing((PingMessage) m);
        }
        else{
            System.err.println("Message not recognized; ignoring it");
        }
    }

    /**
     * Handles the incoming message from the client and adds it to the Queue.
     *
     * @param m the message received from the client
     * @param client  the client sending the message
     * @throws RemoteException if a remote communication error occurs
     */
    @Override
    public void handleMessage(Message m, Client client) throws RemoteException {
        ServerImplementation.logger.log(Level.INFO, "Recieved message from client: " + m.getClass());
        addToMessagesQueue(m,client);
    }

    /**
     * Checks if the game has started.
     *
     * @return true if the game has started, false otherwise
     */
    public boolean isGameStarted() {
        return this.controller.isGameStarted();
    }

    /**
     * Deletes the game associated with the specified players.
     *
     * @param players the list of players in the game
     */
    public void deleteGame(List<String> players,List<GameListener> listeners) {
        this.serverImplementation.deleteGame(players);
        for(GameListener listener : listeners) listener.update(new GameServerMessage(serverImplementation));
    }

    /**
     * Reconnects the player with the specified username and assigns the specified listener to the game.
     *
     * @param username the username of the player reconnecting
     * @param listener the listener to assign to the game
     */
    public void reconnect(String username, GameListener listener) {
        this.controller.reconnect(username, listener);
        synchronized (playingUsernames) {
            playingUsernames.add(username);
        }
        synchronized (disconnectedUsernames) {
            disconnectedUsernames.remove(username);
        }
    }

    /**
     * Adds a player with the specified username and assigns the specified listener to the game.
     *
     * @param username the username of the player to add
     * @param listener the listener to assign to the game
     * @return true if the player is added successfully, false otherwise
     */
    public boolean addPlayer(String username, GameListener listener) {
        boolean res;
        synchronized (playingUsernames) {
            res = this.controller.addPlayer(username, listener);
            listener.update(new GameServerMessage(this));
            playingUsernames.add(username);
        }
        return res;
    }

    /**
     * Gets the number of active players in the game.
     *
     * @return the number of active players
     */
    public int getNumOfActivePlayers() {

        return this.controller.getNumOfActivePlayers();
    }


    /**
     * Disconnects the player with the specified username.
     *
     * @param username the username of the player to disconnect
     */
    private void disconnect(String username) {
        synchronized (playingUsernames) {
            if( !playingUsernames.contains(username) ) return;

            playingUsernames.remove(username);
            if( isGameStarted() ) {
                synchronized (disconnectedUsernames) {
                    disconnectedUsernames.add(username);
                    controller.disconnect(username);
                }
                serverImplementation.disconnect(username, this);
            }
            else {
                GameListener disconnected_listener = controller.kick(username);
                serverImplementation.kick(username, this);
                disconnected_listener.update(new GameServerMessage(serverImplementation));
            }

        }
    }

    /**
     * Handles the turn action of the player with the specified username.
     *
     * @param username     the username of the player
     * @param chosenTiles  the chosen tiles for the turn
     * @param column       the column to place the chosen tiles
     */
    private void doTurn(String username, Coordinates[] chosenTiles, int column) {
        synchronized (playingUsernames) {
            if( !playingUsernames.contains(username) || !isGameStarted() ) return; // message is ignored
            controller.doTurn(username,chosenTiles,column);
        }
    }

}
