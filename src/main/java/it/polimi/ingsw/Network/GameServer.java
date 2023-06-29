package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Exceptions.UsernameNotFoundException;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Utilities.Config;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.logging.Level;

/**
 * The {@code GameServer} class represents the server component responsible for managing the game lobby and the game itself.
 * It acts as a lobby before the game starts, allowing players to join and wait for the game to begin. Once the game starts,
 * it serves as the server for the ongoing game, handling player actions and managing the game state.
 *
 * The {@code GameServer} implements the {@link Server} interface, which defines the communication protocol between the server and clients.
 * It extends the `UnicastRemoteObject` class to enable remote method invocation (RMI) functionality.
 *
 * The {@code GameServer} maintains a reference to the {@link Controller} responsible for managing the game logic and state. It also holds
 * references to the {@link ServerImplementation} and acts as a mediator between the game and the server.
 *
 * The {@code GameServer} keeps track of the players in the lobby using the `playingUsernames` list and the disconnected players using
 * the `disconnectedUsernames` list. The `playingUsernames` list contains the usernames of players who are currently connected
 * and actively participating in the game. The `disconnectedUsernames` map stores the usernames of players who were previously
 * connected but got disconnected. The map holds the username as the key and the corresponding {@code GameServer} object as the value,
 * allowing for easy reconnection of players.
 *
 * The {@code GameServer} class provides methods to handle incoming messages from clients. It distinguishes between turn action messages
 * and disconnect messages. For turn action messages, it calls the `doTurn` method to process the player's turn. For disconnect
 * messages, it triggers the `disconnect` method to handle the player's disconnection.
 *
 * The class also provides methods to check if the game has started, delete the game and remove players from the game, reconnect
 * disconnected players, add new players to the lobby, and retrieve the number of active players in the game.
 *
 * Note: The {@code GameServer} class is meant to be used in a distributed environment and supports both RMI and socket communication
 * protocols. It acts as a server for RMI-based clients and uses the {@link it.polimi.ingsw.Network.Middleware.ClientSkeleton} class to handle socket-based clients.
 * The {@link ServerImplementation} class is responsible for managing the server-side logic and communication protocols.
 */
public class GameServer extends UnicastRemoteObject implements Server {
    private final Controller controller;
    private ServerImplementation serverImplementation = null;
    private final String[] playingUsernames;
    private final Queue<Message> receivedMessages = new LinkedList<>();
    private int nextIdPing = 0;
    private final int[] idPingToBeAnswered;
    private final Timer[] playersTimers;
    private final TimerTask[] playersTimersTasks;


    /**
     * Creates a new {@code GameServer} object by setting the number of players in the game and initializing the {@code Controller}.
     * It also starts the thread responsible for handling incoming messages from clients and pings.
     *
     * @param numOfPlayers The number of players in the game.
     * @throws RemoteException If the remote method invocation fails.
     */
    public GameServer(int numOfPlayers) throws RemoteException {
        super();
        this.serverImplementation = ServerImplementation.getInstance();
        this.controller = new Controller(new Game(numOfPlayers), this);
        this.playingUsernames = new String[numOfPlayers];

        new Thread(){
            @Override
            public void run(){

                //noinspection InfiniteLoopStatement
                while(true){

                    if( isMessagesQueueEmpty() ) continue;

                    Message message = popFromMessagesQueue();
                    try {
                        effectivelyHandleMessage(message);
                    }catch (RemoteException ex){
                        ServerImplementation.logger.log(Level.SEVERE, "Failed to handle message of client: " + ex.getMessage());
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
                //noinspection InfiniteLoopStatement
                while(true){
                    try {
                        Thread.sleep(1000 * Config.getInstance().getPingInterval() * 3 / 2);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    ServerImplementation.logger.log(Level.INFO, "NEW ROUND OF PINGS");
                    synchronized (playingUsernames){
                        for(int index = 0;index<playingUsernames.length;index++){

                            final String username = playingUsernames[index];

                            if(username == null) continue;
                            ServerImplementation.logger.log(Level.INFO,"Pinging player " + username + "(index: " + index + ") for ping #" + nextIdPing);

                            GameListener listener;
                            try{
                                listener = controller.getListener(username);
                            }catch (UsernameNotFoundException ex){
                                continue;
                            }

                            PingMessage pingMessage = new PingMessage(nextIdPing);
                            idPingToBeAnswered[index] = nextIdPing;
                            ServerImplementation.logger.log(Level.INFO,"Set ping #" + pingMessage.getPingNumber() + " for player " + username + "(index: " + index + ")");

                            nextIdPing = (nextIdPing+1) % 100000;

                            playersTimers[index] = new Timer();

                            final int indexThread = index;
                            playersTimersTasks[index] = new TimerTask() {
                                @Override
                                public void run() {
                                    synchronized (playingUsernames){
                                        //player has not answered to ping
                                        //disconnect player
                                        ServerImplementation.logger.log(Level.INFO, username + " has not answered to ping #" + idPingToBeAnswered[indexThread] + " in time; DISCONNECTING HIM");
                                        disconnect(username);


                                    }
                                }
                            };

                            playersTimers[index].schedule(playersTimersTasks[index], 1000 * Config.getInstance().getPingInterval());

                            try{
                                listener.update(pingMessage);
                            }catch (Exception ex){
                                ServerImplementation.logger.log(Level.SEVERE, "Cannot send ping message to client: " + ex.getMessage());
                                continue;
                            }

                            ServerImplementation.logger.log(Level.INFO,"Sent ping #" + pingMessage.getPingNumber() + " to " + username);


                        }
                    }
                }
            }
        }.start();
    }

    /**
     * Handles the specified {@code Message} by calling the appropriate method.
     *
     * @param m the {@link Message} to handle
     * @param client the {@link Client} that sent the message
     * @throws RemoteException if the remote method invocation fails
     */
    @Override
    public void handleMessage(Message m, Client client) throws RemoteException {
        ServerImplementation.logger.log(Level.INFO, "Received message from client: " + m.getClass());
        addToMessagesQueue(m);
    }

    /**
     * Deletes the game associated with the specified {@code Player}s.
     *
     * @param players the list of {@link it.polimi.ingsw.Model.Player} in the game
     * @param listeners the {@link GameListener} to notify
     */
    public void deleteGame(List<String> players,List<GameListener> listeners) {
        this.serverImplementation.deleteGame(players);
        for(GameListener listener : listeners) listener.update(new GameServerMessage(serverImplementation));
    }

    /**
     * Reconnects the player with the specified username and assigns him the specified {@code GameListener}.
     *
     * @param username the username of the player reconnecting
     * @param listener the {@link GameListener} to assign to the game
     */
    protected void reconnect(String username, GameListener listener) {
        this.controller.reconnect(username, listener);
        synchronized (playingUsernames) {
            for(int i = 0;i<playingUsernames.length;i++){
                if(playingUsernames[i] == null){
                    playingUsernames[i] = username;
                    break;
                }
            }
        }
    }

    /**
     * Adds a player with the specified username and assigns him the given {@code GameListener}.
     *
     * @param username the username of the player to add
     * @param listener the {@link GameListener} to assign to the game
     * @return true if the player is added successfully, false otherwise
     */
    protected boolean addPlayer(String username, GameListener listener) {
        boolean res;
        synchronized (playingUsernames) {
            res = this.controller.addPlayer(username, listener);
            listener.update(new GameServerMessage(this));
            for(int i = 0;i<playingUsernames.length;i++){
                if(playingUsernames[i] == null){
                    playingUsernames[i] = username;
                    break;
                }
            }
        }
        return res;
    }

    /**
     * Gets the number of active players in the game.
     *
     * @return the number of active players
     */
    protected int getNumOfActivePlayers() {

        return this.controller.getNumOfActivePlayers();
    }

    /**
     * Disconnects the player with the specified username.
     *
     * @param username the username of the player to disconnect
     */
    private void disconnect(String username) {
        synchronized (playingUsernames) {
            int i = 0;
            for(i=0;i<playingUsernames.length;i++){
                if(playingUsernames[i] != null && playingUsernames[i].equals(username)){
                    playingUsernames[i] = null;
                    break;
                }
            }
            if(i == playingUsernames.length) return; // username not found
            if( isGameStarted() ) {
                controller.disconnect(username);
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
     * Checks if the game has started.
     *
     * @return true if the game has started, false otherwise
     */
    private boolean isGameStarted() {
        return this.controller.isGameStarted();
    }

    /**
     * Adds a {@code Message} to the message queue.
     *
     * @param m The {@link Message} to be added to the queue.
     */
    private void addToMessagesQueue(Message m) {
        synchronized (receivedMessages) {
            receivedMessages.add(m);
        }
    }

    /**
     * Checks if the message queue is empty.
     *
     * @return {@code true} if the message queue is empty, {@code false} otherwise.
     */
    private boolean isMessagesQueueEmpty() {
        synchronized (receivedMessages) {
            return receivedMessages.isEmpty();
        }
    }

    /**
     * Removes and returns a {@code Message} from the message queue.
     *
     * @return The {@link Message} or {@code null} if the queue is empty.
     */
    private Message popFromMessagesQueue() {
        synchronized (receivedMessages) {
            return receivedMessages.poll();
        }
    }

    /**
     * Handles a received {@code Message} and performs appropriate actions based on its type.
     * This method is called to process messages received by the server.
     *
     * @param m      The {@link Message} to be effectively handled.
     * @throws RemoteException If a remote exception occurs during message handling.
     */
    private void effectivelyHandleMessage(Message m) throws RemoteException {
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
            handlePingResponse((PingMessage) m);
        }
        else{
            System.err.println("Message not recognized; ignoring it");
        }
    }

    /**
     * Handles the ping response, it resets the ping timer for the player who sent the response
     *
     * @param message the {@link PingMessage} to be handled
     */
    private void handlePingResponse(PingMessage message){
        //get index of element in idPingToBeAnswered with value message.getpingNumber()
        ServerImplementation.logger.log(Level.INFO,"Started to handle ping #" + message.getPingNumber());
        synchronized (playingUsernames){
            int index = -1;
            for( int i=0; i<idPingToBeAnswered.length; i++ ){
                boolean resCheck = idPingToBeAnswered[i] == message.getPingNumber();
                if( resCheck ){
                    index = i;
                    ServerImplementation.logger.log(Level.INFO,"Ping #" + message.getPingNumber() + " of player " + playingUsernames[i] + "(player index i=" + i + ")");
                    break;
                }
            }

            if( index == -1 )
                return;

            if( playersTimers[index] != null ){
                ServerImplementation.logger.log(Level.INFO,"Reset timer and ping for player " + playingUsernames[index] + " (player index i="+index + ")");
                playersTimersTasks[index].cancel();
                playersTimers[index].cancel();
            }

        }

    }

    /**
     * Handles the turn action of the player with the specified username.
     *
     * @param username     the username of the player
     * @param chosenTiles  the {@link Coordinates} of the chosen tiles for the turn
     * @param column       the column to place the chosen tiles
     */
    private void doTurn(String username, Coordinates[] chosenTiles, int column) {
        synchronized (playingUsernames) {
            int i = 0;
            for(i=0;i<playingUsernames.length;i++){
                if(playingUsernames[i] != null && playingUsernames[i].equals(username)){
                    break;
                }
            }
            if( i == playingUsernames.length || !isGameStarted() ) return; // message is ignored
            controller.doTurn(username,chosenTiles,column);
        }
    }

}
