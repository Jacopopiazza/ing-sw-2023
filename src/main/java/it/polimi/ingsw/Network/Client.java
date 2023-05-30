package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;

import java.io.Serializable;
import java.rmi.RemoteException;

/**
 * The Client interface represents a client in a network communication.
 */
public interface Client extends Serializable {
    /**
     * Updates the client with the specified message.
     *
     * @param m the message to update the client with
     * @throws RemoteException if a remote communication error occurs
     */
    void update(Message m) throws RemoteException;
}
