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

public abstract class ClientManager implements Runnable, View
{
    private Client client;

    List<ViewListener> listeners;

    public ClientManager() {
        this.listeners = new ArrayList<>();
    }

    protected void setUpRMIClient() throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry();
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
                        try {
                            serverStub.close();
                        } catch (RemoteException ex) {
                            System.err.println("Cannot close connection with server. Halting...");
                        }
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
        System.out.println("Sending message boiiii");
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
        notifyListeners(new ReconnectMessage(username,client));
    }

    protected void doConnect(String username, int numOfPlayers){
        notifyListeners(new RegisterMessage(username,client,numOfPlayers));
    }


}
