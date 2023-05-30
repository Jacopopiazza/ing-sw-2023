package it.polimi.ingsw.Messages;

import java.io.Serializable;

/**
 * The MissingUsernameMessage class represents a message indicating that the username is missing or null.
 * It implements the Message interface and is serializable.
 */
public class MissingUsernameMessage implements Message, Serializable {
    private final String message = "Username is null";

    /**
     * Returns the string representation of the message.
     *
     * @return The message string.
     */
    @Override
    public String toString(){
        return message;
    }
}

