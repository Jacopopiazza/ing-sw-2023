package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;

import java.rmi.RemoteException;

public interface Client {
    void update(Message m) throws RemoteException;
}
