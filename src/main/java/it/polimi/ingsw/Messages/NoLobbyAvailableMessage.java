package it.polimi.ingsw.Messages;

import java.io.Serializable;

/**
 * The NoLobbyAvailableMessage class represents a message indicating that no lobby is available.
 * It implements the Message interface and is serializable.
 */
public class NoLobbyAvailableMessage implements Message, Serializable {
    private final String message = "No lobby available";

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
