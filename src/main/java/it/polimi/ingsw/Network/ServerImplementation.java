package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Utilities.Config;
import it.polimi.ingsw.Network.Middleware.ClientSkeleton;
import it.polimi.ingsw.Tuple;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;

/**
 * The {@code ServerImplementation} class represents the server implementation for the game. It handles the communication
 * with clients using both RMI (Remote Method Invocation) and socket protocols.
 *
 * The class implements the {@link Server} interface, which defines the communication methods that clients can invoke.
 * It extends the {@link UnicastRemoteObject} class to enable remote method invocation (RMI) functionality.
 *
 * The {@code ServerImplementation} class maintains a list of connected players (`playingUsernames`) and a map of disconnected
 * players (`disconnectedUsernames`). It also keeps a queue of lobbies waiting to start (`lobbiesWaitingToStart`).
 *
 * The class provides methods to handle incoming messages from clients. The `handleMessage` method processes register
 * messages to connect clients to their respective `GameServer` instances and reconnect messages to reconnect clients
 * to their previous {@link GameServer} instances.
 *
 * The class also provides methods to delete a game, disconnect a player, kick a player from a lobby, and handle player
 * reconnection. These methods ensure the appropriate management of players and game instances.
 *
 * The {@code ServerImplementation} class supports both RMI and socket communication protocols. The `startRMI` method starts
 * the RMI server, while the `startSocket` method starts the socket server. Incoming socket connections are handled
 * by the {@link ClientSkeleton} class.
 *
 * The class includes a singleton pattern to ensure that only one instance of the server exists. The `getInstance`
 * method returns the singleton instance of the server.
 *
 * The `main` method starts the server by creating the singleton instance and starting the RMI and socket server threads.
 * It also sets up the logger to log server activities.
 *
 * Note: The {@code ServerImplementation} class assumes that the game logic and state are managed by the {@link GameServer} class,
 * which is responsible for handling the game lobby and the game itself.
 */
public class ServerImplementation extends UnicastRemoteObject implements Server {
    private static ServerImplementation instance;
    public static final Logger logger = Logger.getLogger("ServerImplementation");
    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private List<String> playingUsernames; // to disconnect
    private Map<String, GameServer> disconnectedUsernames;
    private Queue<GameServer> lobbiesWaitingToStart;
    private Queue<Tuple<Message, Client>> receivedMessages = new LinkedList<>();

    /**
     * Returns the singleton instance of the server. If the instance does not exist, it creates a new one.
     *
     * @return the singleton instance of the {@code ServerImplementation}
     */
    public static ServerImplementation getInstance() throws RemoteException{
        if( instance == null ) {
            instance = new ServerImplementation();
        }
        return instance;
    }

    /**
     * The entry point of the server application. It creates an instance of the server, starts the RMI and socket servers,
     * and logs the server start.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args){

        Thread socketThread = new Thread() {
            @Override
            public void run() {
                logger.log(Level.INFO, "Start Socket service on port " + Config.getInstance().getSocketPort());

                try {
                    startSocket();
                } catch (RemoteException e) {
                    System.err.println("Cannot start socket server, this protocol will be disabled");
                    System.err.println(e.getMessage());
                }
            }
        };
        socketThread.start();
        Thread rmiThread = new Thread() {
            @Override
            public void run() {
                logger.log(Level.INFO, "Start RMI service on port " + Config.getInstance().getRmiPort());
                try {
                    startRMI();
                } catch (RemoteException e) {
                    System.err.println("Cannot start RMI. This protocol will be disabled.");
                }
            }
        };
        logger.log(Level.INFO, "Start server");

        System.setProperty("java.rmi.server.hostname", Config.getInstance().getIpServer());

        rmiThread.start();

    }

    /**
     * Starts the RMI server by creating the RMI registry and binding the server instance to a name in the registry.
     *
     * @throws RemoteException if there is an error in remote method invocation
     */
    private static void startRMI() throws RemoteException {
        LocateRegistry.createRegistry(Config.getInstance().getRmiPort());
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("G26-MyShelfie-Server", getInstance());
    }

    /**
     * Starts the socket server by creating a {@code ServerSocket} and accepting incoming socket connections. Each connection is
     * handled by a separate thread using the {@link ClientSkeleton} class.
     *
     * @throws RemoteException if there is an error in remote method invocation
     */
    private static void startSocket() throws RemoteException {
        try (ServerSocket serverSocket = new ServerSocket(Config.getInstance().getSocketPort())) {
            while(true) {
                logger.log(Level.INFO, "Waiting for a new socket...");
                Socket socket = serverSocket.accept();
                logger.log(Level.INFO, "New socket accepted");
                getInstance().executorService.execute(() -> {
                    try {
                        ClientSkeleton clientSkeleton = new ClientSkeleton(getInstance(), socket);
                        while(true)
                            clientSkeleton.receive();
                    } catch (RemoteException e) {
                        logger.log(Level.SEVERE, "Error receiving from client");
                    } finally {
                        logger.log(Level.INFO, "Socket client disconnected");
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

    /**
     * Sets up the logger for the server. It configures the logger's level, creates a file handler for logging to a file,
     * sets up a formatter for the file handler, creates a console handler, and adds the handlers to the logger.
     */
    private static void setUpLogger(){
        FileHandler fileHandler;
        try{
            fileHandler = new FileHandler("server.log");
        } catch (IOException ex){
            System.err.println("Cannot create log file. Halting...");
            return;
        }
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.SEVERE);
        logger.addHandler(fileHandler);
    }

    /**
     * Constructs a new instance of the {@code ServerImplementation} class for the singleton pattern.
     * It initializes the active and disconnected players collections, as well as the collection for the lobbies.
     *
     * @throws RemoteException if there is an error in remote method invocation
     */
    private ServerImplementation() throws RemoteException {
        super();
        setUpLogger();

        playingUsernames = new ArrayList<>();
        disconnectedUsernames = new HashMap<>();
        lobbiesWaitingToStart = new LinkedBlockingQueue<>();

        new Thread(){
            @Override
            public void run(){

                while(true){
                    if( isMessagesQueueEmpty() )
                        continue;
                    Tuple<Message,Client> tuple = popFromMessagesQueue();
                    try {
                        effectivelyHandleMessage(tuple.getFirst(), tuple.getSecond());
                    } catch (RemoteException ex){
                        logger.log(Level.SEVERE, "Failed to handle message from client: " + ex.getMessage());
                    }
                }
            }

        }.start();
    }

    /**
     * Handles the incoming {@code Message} from a {@code Client}. It adds the message to the queue of messages to be processed.
     *
     * @param m      the incoming {@link Message}
     * @param client the {@link Client} object associated with the message
     * @throws RemoteException if there is an error in remote method invocation
     */
    public void handleMessage(Message m, Client client) throws RemoteException {
        logger.log(Level.INFO, "Received message from client: " + m.getClass());
        addToMessagesQueue(m,client);
    }

    /**
     * Deletes the game associated with the given list of {@code Player}s. Removes the players from the active and
     * disconnected players collections.
     *
     * @param players the list of {@link it.polimi.ingsw.Model.Player} to be removed from the game
     */
    protected void deleteGame(List<String> players) {
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

    /**
     * Disconnects a player from the server. Updates collections of active and disconnected players accordingly.
     *
     * @param username   the username of the player to disconnect
     * @param gameServer the {@link GameServer} object associated with the player
     */
    protected void disconnect(String username, GameServer gameServer) {
        logger.log(Level.INFO, "Player " + username + " disconnected");
        synchronized (playingUsernames) {
            if( !playingUsernames.contains(username) ) return;
            playingUsernames.remove(username);
        }
        synchronized (disconnectedUsernames) {
            disconnectedUsernames.put(username, gameServer);
        }
    }

    /**
     * Kicks a player from the specified lobby. If the lobby becomes empty after the player is kicked, it is removed from
     * the lobbies queue.
     *
     * @param username the username of the player to kick
     * @param lobby    the {@link GameServer} object representing the lobby
     */
    protected void kick(String username, GameServer lobby) {
        logger.log(Level.INFO, "Player " + username + " kicked");
        synchronized (playingUsernames) {
            playingUsernames.remove(username);
            if( lobby == lobbiesWaitingToStart.peek() && lobby.getNumOfActivePlayers() == 0 ) {
                lobbiesWaitingToStart.poll();
                while( lobbiesWaitingToStart.peek() != null && lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0 )
                    lobbiesWaitingToStart.poll();
            }
        }
    }

    /**
     * Handles the reconnection request from a player. It checks if the player is already logged in or if the player has a
     * previous {@code GameServer} associated with their username. If the conditions are met, the player is reconnected to the
     * respective {@link GameServer}.
     *
     * @param username the username of the player requesting reconnection
     * @param listener the {@link GameListener} object to receive updates from the {@link GameServer}
     */
    private void reconnect(String username, GameListener listener) {
        logger.log(Level.INFO, "Reconnect request for " + username );
        synchronized (playingUsernames) {
            synchronized (disconnectedUsernames) {
                if( playingUsernames.contains(username) ) {
                    logger.log(Level.INFO, "A player with username:  " + username + " is already logged in");
                    listener.update(new TakenUsernameMessage());
                    return;
                }
                else if( !(disconnectedUsernames.containsKey(username)) ) {
                    logger.log(Level.INFO, "No player ever played with username:  " + username);
                    listener.update(new UsernameNotFoundMessage());
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

    /**
     * Handles the registration request from a player. If the parameters are valid and there is a lobby available, it
     * registers the player by adding them to the respective GameServer. If the number of players is 1, it tries to join an
     * existing lobby; otherwise, it creates a new lobby.
     *
     * @param username     the username of the player
     * @param numOfPlayers the number of players in the lobby (1 for joining an existing lobby, >1 for a new lobby)
     * @param listener     the {@link GameListener} object to receive updates from the {@link GameServer}
     * @throws RemoteException if there is an error in remote method invocation
     */
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
        logger.log(Level.INFO, "Register request for player with username:  " + username + " parameters were valid. Logging player in");

        synchronized (playingUsernames) {
            synchronized (disconnectedUsernames) {
                if( playingUsernames.contains(username) || disconnectedUsernames.containsKey(username) ) {
                    listener.update(new TakenUsernameMessage());
                    return;
                }
            }
            synchronized (lobbiesWaitingToStart){
                if( numOfPlayers != 1 ){
                    GameServer lobby = new GameServer(numOfPlayers);
                    lobby.addPlayer(username, listener);
                    playingUsernames.add(username);
                    lobbiesWaitingToStart.add(lobby);
                }
                else {
                    if( lobbiesWaitingToStart.peek() != null ) {
                        GameServer lobby = lobbiesWaitingToStart.peek();
                        if( lobby.addPlayer(username, listener) ) { // full
                            lobbiesWaitingToStart.poll();
                            while( lobbiesWaitingToStart.peek() != null && lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0 )
                                lobbiesWaitingToStart.poll();
                        }
                        playingUsernames.add(username);
                    }
                    else listener.update(new NoLobbyAvailableMessage());
                }
            }
        }
    }
    /**
     * Adds a {@code Message} and its associated {@code Client} to the message queue.
     *
     * @param m The {@link Message} to be added to the queue.
     * @param c The {@link Client} associated with the message.
     */
    private void addToMessagesQueue(Message m, Client c) {
        synchronized (receivedMessages) {
            receivedMessages.add(new Tuple<>(m, c));
        }
    }

    /**
     * Checks if the messages queue is empty.
     *
     * @return {@code true} if the message queue is empty, {@code false} otherwise.
     */
    private boolean isMessagesQueueEmpty() {
        synchronized (receivedMessages) {
            return receivedMessages.isEmpty();
        }
    }

    /**
     * Removes and returns a message from the {@code Message} queue.
     *
     * @return The {@link Message} and associated {@link Client} as a {@link Tuple}, or {@code null} if the queue is empty.
     */
    private Tuple<Message, Client> popFromMessagesQueue() {
        synchronized (receivedMessages) {
            return receivedMessages.poll();
        }
    }

    /**
     * Handles a received {@code Message} and performs appropriate actions based on its type.
     * This method is called to process {@code Message}s received by the server.
     *
     * @param m      The {@link Message} to be effectively handled.
     * @param client The {@link Client} associated with the message.
     * @throws RemoteException If a remote exception occurs during message handling.
     */
    private void effectivelyHandleMessage(Message m, Client client) throws RemoteException {
        if( m instanceof RegisterMessage ) {
            register( ((RegisterMessage) m).getUsername(), ((RegisterMessage) m).getNumOfPlayers(), ( (message) -> {
                try {
                    client.update(message);
                } catch (RemoteException e) {
                    System.err.println("Cannot send message to client of: " + ((RegisterMessage)m).getUsername());
                    System.err.println("Error: " + e.getMessage());
                }
            }) );
        }
        else if( m instanceof ReconnectMessage) {
            reconnect( ((ReconnectMessage) m).getUsername(), ( (message) -> {
                try {
                    client.update(message);
                } catch (RemoteException e) {
                    System.err.println("Cannot send message to client of: " + ((ReconnectMessage) m).getUsername());
                    System.err.println("Error: " + e.getMessage());
                }
            }) );
        }
        else System.err.println("Message not recognized; ignoring it");
    }

}