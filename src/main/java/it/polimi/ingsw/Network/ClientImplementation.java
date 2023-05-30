package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.View.View;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * The ClientImplementation class is an implementation of the Client interface.
 */
public class ClientImplementation extends UnicastRemoteObject implements Client {
    private View view;

    /**
     * Constructs a ClientImplementation instance with the specified view and server.
     *
     * @param view   the view associated with the client
     * @param server the server handling the client's messages
     * @throws RemoteException if a remote communication error occurs
     */
    public ClientImplementation(View view, Server server) throws RemoteException{
        super();
        this.view = view;

        view.addListener((message) -> {
            try {
                server.handleMessage(message, (Client)this);
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
                System.err.println(e.getCause());
                return;
            }


        });
    }

    /**
     * Updates the client with the specified message.
     *
     * @param m the message to update the client with
     * @throws RemoteException if a remote communication error occurs
     */


    @Override
    public void update(Message m) throws RemoteException {
        this.view.update(m);
    }


}
