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
        Registry registry = LocateRegistry.getRegistry(1111);
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
        System.out.println("Notifying ClientManager listeners");
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
        System.out.println("Sending reconnect message");
        notifyListeners(new ReconnectMessage(username,client));
    }

    protected void doConnect(String username, int numOfPlayers){
        System.out.println("Sending register message");
        notifyListeners(new RegisterMessage(username,client,numOfPlayers));
    }


}
