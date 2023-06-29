package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The Server interface represents the server in the network communication.
 * It extends the {@link Remote} interface and provides a method for handling a {@link Message} from a {@link Client}.
 */
public interface Server extends Remote {
    /**
     * Handles the incoming {@code Message} from the {@code Client}.
     *
     * @param m the {@link Message} received from the client
     * @param client  the {@link Client} sending the message
     * @throws RemoteException if a remote communication error occurs
     */
    void handleMessage( Message m, Client client) throws RemoteException;
}
