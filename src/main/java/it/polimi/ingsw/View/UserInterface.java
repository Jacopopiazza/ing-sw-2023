package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidIPAddress;
import it.polimi.ingsw.Exceptions.InvalidPort;
import it.polimi.ingsw.Messages.DisconnectMessage;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.ReconnectMessage;
import it.polimi.ingsw.Messages.RegisterMessage;
import it.polimi.ingsw.Model.Utilities.Config;
import it.polimi.ingsw.Model.Utilities.IPAddressValidator;
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
 */
public abstract class UserInterface implements Runnable, View
{
    protected Client client;
    List<ViewListener> listeners;

    /**
     * Sets up the RMI client by obtaining the server stub from the RMI registry.
     *
     * @throws RemoteException    if there is a remote communication error
     * @throws NotBoundException  if the server stub is not found in the registry
     */
    public UserInterface() {
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
        try{
            setUpRMIClient("localhost");
        }catch (RemoteException ex){
            throw ex;
        }
        catch (NotBoundException ex){
            throw ex;
        }
        catch (InvalidIPAddress ex){
            //Do nothing, impossible to happen
            throw new RuntimeException("Impossible to happen, but it somehow happened");
        }

    }

    /**

     Sets up the RMI client by connecting to the RMI registry located at the specified IP and port.
     Throws exceptions if the IP address or port is invalid or if the RMI registry is not bound.

     @param ip the IP address of the RMI registry

     @throws RemoteException if a remote communication error occurs

     @throws NotBoundException if the RMI registry is not bound

     @throws InvalidIPAddress if the provided IP address is invalid

     @throws InvalidPort if the provided port number is invalid
     */
    protected void setUpRMIClient(String ip) throws RemoteException, NotBoundException, InvalidIPAddress {

        if(ip == null || ip.isEmpty() || (!IPAddressValidator.isValidIPAddress(ip) && !IPAddressValidator.isValidURL(ip))) {
            throw new InvalidIPAddress("Invalid IP/URL address");
        }

        Registry registry = LocateRegistry.getRegistry(ip, Config.getInstance().getRmiPort());
        Server server = (Server) registry.lookup("G26-MyShelfie-Server");

        this.client = ClientImplementation.getInstance(this, server);


    }


    protected void setUpSocketClient() throws RemoteException, NotBoundException {


        try{
            setUpSocketClient("localhost");
        }catch (RemoteException ex){
            throw ex;
        }
        catch (NotBoundException ex){
            throw ex;
        }
        catch (InvalidIPAddress ex){
            //Do nothing, impossible to happen
            throw new RuntimeException("Impossible to happen, but it somehow happened");
        }


    }

    protected void setUpSocketClient(String ip) throws RemoteException, NotBoundException, InvalidIPAddress{

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
     * Adds a ViewListener to the list of listeners. The listener will be notified when events occur.
     *
     * @param listener the ViewListener to add
     */
    @Override
    public void addListener(ViewListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Notifies all the registered listeners with the specified Message. The listeners will handle the message accordingly.
     *
     * @param m the Message to notify the listeners with
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
     * Clears the list of listeners, removing all registered listeners.
     */
    public void clearListeners(){
        synchronized (listeners) {
            listeners.clear();
        }
    }

    /**
     * Sends a reconnect message to the listeners.
     *
     * @param username the username of the player to reconnect
     */
    protected void doReconnect(String username){
        ClientImplementation.logger.log(Level.INFO,"Sending reconnect message");
        notifyListeners(new ReconnectMessage(username));
    }

    /**
     * Sends a register message to the listeners to connect to the game.
     *
     * @param username     the username of the player
     * @param numOfPlayers the number of players in the game
     */
    protected void doConnect(String username, int numOfPlayers){
        ClientImplementation.logger.log(Level.INFO,"Sending register message with numOfPlayer=" + numOfPlayers);
        notifyListeners(new RegisterMessage(username,numOfPlayers));
    }

    /**
     * Notifies the listeners that a player has quit the game.
     *
     * @param username the username of the player who quit
     */
    protected void doQuit(String username){
        ClientImplementation.logger.log(Level.INFO,username + "left the game");
        notifyListeners(new DisconnectMessage(username));
    }


}
