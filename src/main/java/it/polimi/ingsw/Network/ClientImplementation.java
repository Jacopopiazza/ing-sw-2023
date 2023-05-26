package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.View.View;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientImplementation extends UnicastRemoteObject implements Client {
    private View view;

    public ClientImplementation(View view, Server server) throws RemoteException{
        super();
        this.view = view;

        view.addListener((message) -> {
            try {
                server.handleMessage(message, this);
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
                System.err.println(e.getCause());
                return;
            }
        });
    }

    @Override
    public void update(Message m) throws RemoteException {
        this.view.update(m);
    }


}
