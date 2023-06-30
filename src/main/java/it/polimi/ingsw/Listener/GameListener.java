package it.polimi.ingsw.Listener;

import it.polimi.ingsw.Messages.Message;

/**
 * The GameListener interface represents a listener for game updates.
 * Classes implementing this interface can register themselves as listeners to receive game updates.
 */
public interface GameListener {

    /**
     * This method is called when a game update occurs.
     *
     * @param message The {@link Message} containing the game update information.
     */
    void update(Message message);
}
