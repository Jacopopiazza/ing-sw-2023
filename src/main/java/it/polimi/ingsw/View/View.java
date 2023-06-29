package it.polimi.ingsw.View;

import it.polimi.ingsw.Listener.ViewListener;
import it.polimi.ingsw.Messages.Message;

/**
 * The View interface represents the view component of the client application.
 * It defines methods for updating the view with incoming messages,
 * notifying listeners about new messages, adding listeners, and running the view.
 */
@SuppressWarnings("ALL")
public interface View {

    /**
     * Updates the {@code View} with the given {@code Message}.
     *
     * @param m the {@link Message} to be processed by the view
     */
    void update(Message m);

    /**
     * Notifies the registered {@code ViewListener} about a new {@code Message}.
     *
     * @param m the {@link Message} to be notified to the {@link ViewListener}
     */
    void notifyListeners(Message m);

    /**
     * Remove all registered {@code ViewListener}s.
     *
     */
    void clearListeners();

    /**
     * Adds a {@code ViewListener} to the view.
     *
     * @param listener the {@link ViewListener} to be added
     */
    void addListener(ViewListener listener);

    /**
     * Runs the view, allowing it to interact with the user or display information.
     */
    void run();


}
