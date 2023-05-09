package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;

import java.rmi.RemoteException;

public interface Server {
    public void handleMessage( Message m) throws RemoteException;
}
