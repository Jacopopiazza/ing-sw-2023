package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Server interface represents the server in the network communication.
 */
public interface Server extends Remote {
    /**
     * Handles the incoming message from the client.
     *
     * @param m the message received from the client
     * @param client  the client sending the message
     * @throws RemoteException if a remote communication error occurs
     */
    public void handleMessage( Message m, Client client) throws RemoteException;
}
