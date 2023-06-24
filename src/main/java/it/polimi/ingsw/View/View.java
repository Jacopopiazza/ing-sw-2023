package it.polimi.ingsw.View;

import it.polimi.ingsw.Listener.ViewListener;
import it.polimi.ingsw.Messages.Message;

/**
 * The View interface represents the view component of the client application.
 * It defines methods for updating the view with incoming messages,
 * notifying listeners about new messages, adding listeners, and running the view.
 */
public interface View {

    /**
     * Updates the view with the given message.
     *
     * @param m the message to be displayed or processed by the view
     */
    void update(Message m);

    /**
     * Notifies the registered listeners about a new message.
     *
     * @param m the message to be notified to the listeners
     */
    void notifyListeners(Message m);

    /**
     * Remove all registered listeners.
     *
     */
    void clearListeners();

    /**
     * Adds a listener to the view.
     *
     * @param listener the listener to be added
     */
    void addListener(ViewListener listener);

    /**
     * Runs the view, allowing it to interact with the user or display information.
     */
    void run();

}
