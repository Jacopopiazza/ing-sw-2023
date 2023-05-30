package it.polimi.ingsw.Messages;

import java.io.Serializable;

/**
 * The InvalidActionMessage class represents a message indicating an invalid turn action.
 * It implements the Message interface and is serializable.
 */
public class InvalidActionMessage extends Message {
    private final String message = "Invalid turn action";

    /**
     * Returns the string representation of the invalid action message.
     *
     * @return The message string.
     */
    @Override
    public String toString() {
        return message;
    }
}
