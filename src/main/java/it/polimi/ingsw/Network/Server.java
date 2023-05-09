package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;

public interface Server {
    public void handleMessage( Message m);
}
