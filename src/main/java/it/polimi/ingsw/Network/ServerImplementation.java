package it.polimi.ingsw.Network;

import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Network.Middleware.ClientSkeleton;
import it.polimi.ingsw.Tuple;

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

/**
 * The `ServerImplementation` class represents the server implementation for the game. It handles the communication
 * with clients using both RMI (Remote Method Invocation) and socket protocols.
 *
 * The class implements the `Server` interface, which defines the communication methods that clients can invoke.
 * It extends the `UnicastRemoteObject` class to enable remote method invocation (RMI) functionality.
 *
 * The `ServerImplementation` class maintains a list of connected players (`playingUsernames`) and a map of disconnected
 * players (`disconnectedUsernames`). It also keeps a queue of lobbies waiting to start (`lobbiesWaitingToStart`).
 *
 * The class provides methods to handle incoming messages from clients. The `handleMessage` method processes register
 * messages to connect clients to their respective `GameServer` instances and reconnect messages to reconnect clients
 * to their previous `GameServer` instances.
 *
 * The class also provides methods to delete a game, disconnect a player, kick a player from a lobby, and handle player
 * reconnection. These methods ensure the appropriate management of players and game instances.
 *
 * The `ServerImplementation` class supports both RMI and socket communication protocols. The `startRMI` method starts
 * the RMI server, while the `startSocket` method starts the socket server. Incoming socket connections are handled
 * by the `ClientSkeleton` class.
 *
 * The class includes a singleton pattern to ensure that only one instance of the server exists. The `getInstance`
 * method returns the singleton instance of the server.
 *
 * The `main` method starts the server by creating the singleton instance and starting the RMI and socket server threads.
 * It also sets up the logger to log server activities.
 *
 * Note: The `ServerImplementation` class assumes that the game logic and state are managed by the `GameServer` class,
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
     * Constructs a new instance of the ServerImplementation class. It initializes the playingUsernames, disconnectedUsernames,
     * and lobbiesWaitingToStart collections.
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

                    if( isMessagesQueueEmpty() ) continue;

                    Tuple<Message,Client> tuple = popFromMessagesQueue();
                    try {
                        effectivelyHandlMessage(tuple.getFirst(), tuple.getSecond());
                    } catch (RemoteException ex){
                        logger.log(Level.SEVERE, "Cannot send message to client: " + ex.getMessage());
                    }
                }
            }

        }.start();
    }

    /**
     * Adds a message and its associated client to the message queue.
     *
     * @param m The message to be added to the queue.
     * @param c The client associated with the message.
     */
    private void addToMessagesQueue(Message m, Client c) {
        synchronized (receivedMessages) {
            receivedMessages.add(new Tuple<>(m, c));
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
     * Removes and returns a message from the message queue.
     *
     * @return The message and associated client as a tuple, or {@code null} if the queue is empty.
     */
    private Tuple<Message, Client> popFromMessagesQueue() {
        synchronized (receivedMessages) {
            return receivedMessages.poll();
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

    /**
     * Handles the incoming message from a client. It adds the message to the queue of messages to be processed.
     *
     * @param m      the incoming message
     * @param client the client object associated with the message
     * @throws RemoteException if there is an error in remote method invocation
     */
    public void handleMessage(Message m, Client client) throws RemoteException {

        logger.log(Level.INFO, "Received message from client: " + m.getClass());

        addToMessagesQueue(m,client);
    }

    /**
     * Deletes the game associated with the given list of players. Removes the players from the playingUsernames and
     * disconnectedUsernames collections.
     *
     * @param players the list of players to be removed from the game
     */
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

    /**
     * Disconnects a player from the server. Updates the playingUsernames and disconnectedUsernames collections accordingly.
     *
     * @param username   the username of the player to disconnect
     * @param gameServer the GameServer object associated with the player
     */
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

    /**
     * Kicks a player from the specified lobby. If the lobby becomes empty after the player is kicked, it is removed from
     * the lobbiesWaitingToStart queue.
     *
     * @param username the username of the player to kick
     * @param lobby    the GameServer object representing the lobby
     */
    public void kick(String username, GameServer lobby) {
        logger.log(Level.INFO, "Player " + username + " kicked");

        synchronized (playingUsernames) {
            if( lobby == lobbiesWaitingToStart.peek() && lobby.getNumOfActivePlayers() == 0 ) {
                lobbiesWaitingToStart.poll();
                while( lobbiesWaitingToStart.peek() != null && lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0 ) lobbiesWaitingToStart.poll();
            }
        }
    }

    /**
     * Handles the reconnection request from a player. It checks if the player is already logged in or if the player has a
     * previous GameServer associated with their username. If the conditions are met, the player is reconnected to the
     * respective GameServer.
     *
     * @param username the username of the player requesting reconnection
     * @param listener the GameListener object to receive updates from the GameServer
     */
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
     * registers the player by adding them to the respective GameServer. If the numOfPlayers is 1, it tries to join an
     * existing lobby; otherwise, it creates a new lobby.
     *
     * @param username     the username of the player
     * @param numOfPlayers the number of players in the lobby (1 for joining an existing lobby, >1 for a new lobby)
     * @param listener     the GameListener object to receive updates from the GameServer
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
                    GameServer lobby = new GameServer(this,numOfPlayers);
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
                    }
                    else listener.update(new NoLobbyAvailableMessage());
                }
            }
        }
    }

    /**
     * Starts the RMI server by creating the RMI registry and binding the server instance to a name in the registry.
     *
     * @param server the server instance to be bound to the RMI registry
     * @throws RemoteException if there is an error in remote method invocation
     */
    private static void startRMI(Server server) throws RemoteException {
        LocateRegistry.createRegistry(1099);
        Registry registry = LocateRegistry.getRegistry();
        registry.rebind("G26-MyShelfie-Server", server);
        System.out.println("In attesa di client RMI...");
    }

    /**
     * Starts the socket server by creating a ServerSocket and accepting incoming socket connections. Each connection is
     * handled by a separate thread using the ClientSkeleton class.
     *
     * @param server the server instance
     * @throws RemoteException if there is an error in remote method invocation
     */
    public static void startSocket(Server server) throws RemoteException {
        try (ServerSocket serverSocket = new ServerSocket(1234)) {
            while(true) {
                System.out.println("In attesa di un client via socket...");
                logger.log(Level.INFO, "In attesa di un client via socket...");
                Socket socket = serverSocket.accept();
                System.out.println("Socket Client connected");
                logger.log(Level.INFO, "Nuova connessione via socket accettata");

                instance.executorService.execute(() -> {
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

    /**
     * Returns the singleton instance of the server. If the instance does not exist, it creates a new one.
     *
     * @return the singleton instance of the server
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

    /**
     * Sets up the logger for the server. It configures the logger's level, creates a file handler for logging to a file,
     * sets up a formatter for the file handler, creates a console handler, and adds the handlers to the logger.
     */
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

        // Imposta il livello di logging dei gestori
        fileHandler.setLevel(Level.ALL);

        // Aggiungi i gestori al logger
        logger.addHandler(fileHandler);

    }

}
