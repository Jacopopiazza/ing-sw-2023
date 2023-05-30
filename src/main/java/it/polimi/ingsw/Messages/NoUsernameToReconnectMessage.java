package it.polimi.ingsw.Messages;

import java.io.Serializable;

/**
 * The NoUsernameToReconnectMessage class represents a message indicating that there is no username to reconnect.
 * It implements the Message interface and is serializable.
 */
public class NoUsernameToReconnectMessage implements Message, Serializable {
    private final String message = "No Username to reconnect";

    /**
     * Returns the string representation of the message.
     *
     * @return The message string.
     */
    @Override
    public String toString() {
        return message;
    }
}
