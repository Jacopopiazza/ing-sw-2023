package it.polimi.ingsw.Client;

import it.polimi.ingsw.Messages.DisconnectMessage;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.ReconnectMessage;
import it.polimi.ingsw.Messages.RegisterMessage;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.ClientImplementation;
import it.polimi.ingsw.Network.Middleware.ServerStub;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.View.View;
import it.polimi.ingsw.Listener.ViewListener;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * The ClientManager class is an abstract class responsible for managing the client-side functionality.
 * It provides methods for setting up the RMI and socket clients, adding and notifying listeners,
 * and performing actions such as reconnecting, connecting, and quitting the game.
 */
public abstract class ClientManager implements Runnable, View
{
    protected Client client;
    List<ViewListener> listeners;

    /**
     * Sets up the RMI client by obtaining the server stub from the RMI registry.
     *
     * @throws RemoteException    if there is a remote communication error
     * @throws NotBoundException  if the server stub is not found in the registry
     */
    public ClientManager() {
        this.listeners = new ArrayList<>();
    }

    /**
     * Sets up the socket client by creating a server stub with the specified IP address and port.
     * Starts a new thread to receive messages from the server.
     *
     * @throws RemoteException    if there is a remote communication error
     * @throws NotBoundException  if the server stub is not found in the registry
     */
    protected void setUpRMIClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(1099);
        Server server = (Server) registry.lookup("G26-MyShelfie-Server");

        this.client = new ClientImplementation(this, server);


    }

    protected void setUpSocketClient() throws RemoteException, NotBoundException {
        ServerStub serverStub = new ServerStub("localhost", 1234);
        this.client = new ClientImplementation(this, serverStub);
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
     * Adds a ViewListener to the list of listeners. The listener will be notified when events occur.
     *
     * @param listener the ViewListener to add
     */
    @Override
    public void addListener(ViewListener listener) {
        //synchronized (listeners) {
            listeners.add(listener);
        //}
    }

    /**
     * Notifies all the registered listeners with the specified Message. The listeners will handle the message accordingly.
     *
     * @param m the Message to notify the listeners with
     */
    @Override
    public void notifyListeners(Message m) {
        AppClientImplementation.logger.log(Level.INFO,"Notifying ClientManager listeners");
        //synchronized (listeners) {
            for (ViewListener listener : listeners) {
                listener.handleMessage(m);
            }
        //}
    }

    /**
     * Clears the list of listeners, removing all registered listeners.
     */
    public void cleanListeners(){
        //synchronized (listeners) {
            listeners.clear();
        //}
    }

    /**
     * Sends a reconnect message to the listeners.
     *
     * @param username the username of the player to reconnect
     */
    protected void doReconnect(String username){
        AppClientImplementation.logger.log(Level.INFO,"Sending reconnect message");
        notifyListeners(new ReconnectMessage(username));
    }

    /**
     * Sends a register message to the listeners to connect to the game.
     *
     * @param username     the username of the player
     * @param numOfPlayers the number of players in the game
     */
    protected void doConnect(String username, int numOfPlayers){
        AppClientImplementation.logger.log(Level.INFO,"Sending register message with numOfPlayer=" + numOfPlayers);
        notifyListeners(new RegisterMessage(username,numOfPlayers));
    }

    /**
     * Notifies the listeners that a player has quit the game.
     *
     * @param username the username of the player who quit
     */
    protected void doQuit(String username){
        AppClientImplementation.logger.log(Level.INFO,username + "left the game");
        notifyListeners(new DisconnectMessage(username));
    }


}
