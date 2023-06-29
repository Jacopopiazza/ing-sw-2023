package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidIPAddress;
import it.polimi.ingsw.Messages.DisconnectMessage;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.ReconnectMessage;
import it.polimi.ingsw.Messages.RegisterMessage;
import it.polimi.ingsw.Utilities.Config;
import it.polimi.ingsw.Utilities.IPAddressValidator;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.ClientImplementation;
import it.polimi.ingsw.Network.Middleware.ServerStub;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Listener.ViewListener;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * The UserInterface class is an abstract class responsible for managing the client-side functionality.
 * It provides methods for setting up the RMI and socket clients, adding and notifying listeners,
 * and performing actions such as reconnecting, connecting, and quitting the game.
 * It implements the {@code Runnable} interface, allowing it to be run as a thread; and the {@link View} interface,
 * allowing it to be used as the base class for view implementations.
 */
public abstract class UserInterface implements Runnable, View {
    /**
     * Reference to the client to be filled when the Network Protocol is chosen.
     */
    protected Client client;

    /**
     * Listeners of the {@code View} to be updated.
     */
    protected final List<ViewListener> listeners;

    /**
     * Constructs a new {@code UserInterface} object.
     */
    public UserInterface() {
        this.listeners = new ArrayList<>();
    }

    /**
     * Adds a {@code ViewListener} to the list of listeners. The listener will be notified when events occur.
     *
     * @param listener the {@link ViewListener} to add
     */
    @Override
    public void addListener(ViewListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Clears the list of {@code ViewListener}, removing all registered {@code ViewListener}.
     */
    public void clearListeners(){
        synchronized (listeners) {
            listeners.clear();
        }
    }

    /**
     * Notifies all the registered {@code ViewListener} with the specified {@code Message}. The listeners will handle
     * the message accordingly.
     *
     * @param m the {@link Message} to notify the {@code ViewListener}s with
     */
    @Override
    public void notifyListeners(Message m) {
        ClientImplementation.logger.log(Level.INFO,"Notifying UserInterface listeners");
        synchronized (listeners) {
            for (ViewListener listener : listeners) {
                listener.handleMessage(m);
            }
        }
    }

    /**
     *
     * Sets up the RMI client by connecting to the RMI registry located at the specified IP and port.
     * Throws exceptions if the IP address or port is invalid or if the RMI registry is not bound.
     *
     * @param ip the IP address of the RMI registry.
     * @throws RemoteException if a remote communication error occurs.
     * @throws InvalidIPAddress if the provided IP address is invalid.
     * @throws NotBoundException if an attempt is made to lookup or unbind in the registry a name that has no associated binding.
     */
    protected void setUpRMIClient(String ip) throws RemoteException, NotBoundException, InvalidIPAddress {

        if(ip == null || ip.isEmpty() || (!IPAddressValidator.isValidIPAddress(ip) && !IPAddressValidator.isValidURL(ip))) {
            throw new InvalidIPAddress("Invalid IP/URL address");
        }

        ClientImplementation.logger.log(Level.INFO,"Coonecting to RMI server : " + ip + " on port " + Config.getInstance().getRmiPort() + "...");

        Registry registry = LocateRegistry.getRegistry(ip, Config.getInstance().getRmiPort());
        Server server = (Server) registry.lookup("G26-MyShelfie-Server");

        this.client = ClientImplementation.getInstance(this, server);


    }

    /**
     * Sets up the socket client by connecting to the socket server located at the specified IP and port.
     * Throws exceptions if the IP address or port is invalid or if the socket server is not bound.
     *
     * @param ip the IP address of the socket server.
     * @throws RemoteException if a remote communication error occurs.
     * @throws InvalidIPAddress if the provided IP address is invalid.
     */
    protected void setUpSocketClient(String ip) throws RemoteException, InvalidIPAddress{

        if(ip == null || ip.isEmpty() || (!IPAddressValidator.isValidIPAddress(ip) && !IPAddressValidator.isValidURL(ip))) {
            throw new InvalidIPAddress("Invalid IP/URL address");
        }

        ServerStub serverStub = new ServerStub(ip, Config.getInstance().getSocketPort());
        this.client = ClientImplementation.getInstance(this, serverStub);
        new Thread() {
            @Override
            public void run() {
                while(true) {
                    try {
                        serverStub.receive(client);
                    } catch (RemoteException e) {
                        System.err.println("Cannot receive from server. Stopping...");
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            }
        }.start();


    }

    /**
     * Sends a {@code ReconnectMessage} to the {@code ViewListener}s.
     *
     * @param username the username of the player to reconnect
     */
    protected void doReconnect(String username){
        ClientImplementation.logger.log(Level.INFO,"Sending reconnect message");
        notifyListeners(new ReconnectMessage(username));
    }

    /**
     * Sends a {@code RegisterMessage} to the {@code ViewListener}s to connect to the game.
     *
     * @param username     the username of the player
     * @param numOfPlayers the number of players in the game
     */
    protected void doConnect(String username, int numOfPlayers){
        ClientImplementation.logger.log(Level.INFO,"Sending register message with numOfPlayer=" + numOfPlayers);
        notifyListeners(new RegisterMessage(username,numOfPlayers));
    }

    /**
     * Notifies the {@code ViewListener}s that a player has quit the game.
     *
     * @param username the username of the player who quit
     */
    protected void doQuit(String username){
        ClientImplementation.logger.log(Level.INFO,username + "left the game");
        notifyListeners(new DisconnectMessage(username));
    }


}
