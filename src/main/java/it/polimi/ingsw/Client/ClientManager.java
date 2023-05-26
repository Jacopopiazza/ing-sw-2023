package it.polimi.ingsw.Client;

import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.ReconnectMessage;
import it.polimi.ingsw.Messages.RegisterMessage;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.ClientImplementation;
import it.polimi.ingsw.Network.Middleware.ServerStub;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.View.View;
import it.polimi.ingsw.View.ViewListener;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class ClientManager implements Runnable, View
{
    protected Client client;
    List<ViewListener> listeners;

    public ClientManager() {
        this.listeners = new ArrayList<>();
    }

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

    @Override
    public void addListener(ViewListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    @Override
    public void notifyListeners(Message m) {
        AppClientImplementation.logger.log(Level.INFO,"Notifying ClientManager listeners");
        synchronized (listeners) {
            for (ViewListener listener : listeners) {
                listener.handleMessage(m);
            }
        }
    }

    public void cleanListeners(){
        synchronized (listeners) {
            listeners.clear();
        }
    }

    protected void doReconnect(String username){
        AppClientImplementation.logger.log(Level.INFO,"Sending reconnect message");
        notifyListeners(new ReconnectMessage(username));
    }

    protected void doConnect(String username, int numOfPlayers){
        AppClientImplementation.logger.log(Level.INFO,"Sending register message with numOfPlayer=" + numOfPlayers);
        notifyListeners(new RegisterMessage(username,numOfPlayers));
    }


}
