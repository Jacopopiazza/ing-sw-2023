package it.polimi.ingsw.Listener;

import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Network.Client;

/**
 * The ViewListener interface represents a listener for view messages.
 * Classes implementing this interface can register themselves as listeners to receive view messages.
 */
public interface ViewListener {

     /**
      * This method is called when a view message is received.
      *
      * @param m The view message to be handled.
      */
     void handleMessage(Message m);

}
