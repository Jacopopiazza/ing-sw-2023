package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Server extends Remote {
    public void handleMessage( Message m) throws RemoteException;
}
