package it.polimi.ingsw.Listener;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.ModelView.GameView;

import java.rmi.RemoteException;

public interface GameListener {
    public void update(Message message);

}
