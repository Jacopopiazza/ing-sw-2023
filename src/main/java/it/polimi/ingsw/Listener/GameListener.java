package it.polimi.ingsw.Listener;

import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Network.Client;

public interface GameListener {
    void update(Message message);
}
