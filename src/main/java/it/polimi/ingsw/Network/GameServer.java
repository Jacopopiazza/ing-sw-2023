package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Tuple;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;

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
    private  ServerImplementation serverImplementation = null;
    private List<String> playingUsernames;
    private List<String> disconnectedUsernames;
    private Queue<Tuple<Message, Client>> recievedMessages = new LinkedList<>();


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

                    if( isMessagesQueueEmpty(null) ) continue;

                    Tuple<Message,Client> tuple = popFromMessagesQueue();
                    try {
                        effectivelyHandlMessage(tuple.getFirst(), tuple.getSecond());
                    }catch (RemoteException ex){
                        ServerImplementation.logger.log(Level.SEVERE, "Cannot send message to client: " + ex.getMessage());
                    }
                }
            }

        }.start();
    }

    private void addToMessagesQueue(Message m, Client c) {
        synchronized (recievedMessages) {
            recievedMessages.add(new Tuple<>(m, c));
        }
    }

    private boolean isMessagesQueueEmpty(Message m) {
        synchronized (recievedMessages) {
            return recievedMessages.isEmpty();
        }
    }

    private Tuple<Message, Client> popFromMessagesQueue() {
        synchronized (recievedMessages) {
            return recievedMessages.poll();
        }
    }

    private void effectivelyHandlMessage(Message m, Client client) throws RemoteException {
        if( m instanceof TurnActionMessage ) {
            TurnActionMessage message = (TurnActionMessage) m;
            doTurn(message.getUsername(),message.getChosenTiles(),message.getColumn());
        }
        else if( m instanceof DisconnectMessage ) {
            DisconnectMessage message = (DisconnectMessage) m;
            disconnect(message.getUsername());
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
    public void deleteGame(List<String> players) {
        this.serverImplementation.deleteGame(players);
    }

    /**
     * Reconnects the player with the specified username and assigns the specified listener to the game.
     *
     * @param username the username of the player reconnecting
     * @param listener the listener to assign to the game
     */
    public void reconnect(String username, GameListener listener) {
        this.controller.reconnect(username, listener);
    }

    /**
     * Adds a player with the specified username and assigns the specified listener to the game.
     *
     * @param username the username of the player to add
     * @param listener the listener to assign to the game
     * @return true if the player is added successfully, false otherwise
     */
    public boolean addPlayer(String username, GameListener listener) {
        boolean res = this.controller.addPlayer(username, listener);
        listener.update(new GameServerMessage(this));
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
                controller.kick(username);
                serverImplementation.kick(username, this);
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
