package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Client interface represents a client in a network communication.
 * It extends the {@link Remote} interface and provides a method for updating the client with a {@link Message}.
 */
public interface Client extends Remote {
    /**
     * Updates the client with the specified {@code Message}.
     *
     * @param m the {@link Message} to update the client with
     * @throws RemoteException if a remote communication error occurs
     */
    void update(Message m) throws RemoteException;
}
