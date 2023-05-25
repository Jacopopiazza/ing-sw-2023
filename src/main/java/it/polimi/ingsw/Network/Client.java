package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;

import java.io.Serializable;
import java.rmi.RemoteException;

public interface Client extends Serializable {
    void update(Message m) throws RemoteException;
}
