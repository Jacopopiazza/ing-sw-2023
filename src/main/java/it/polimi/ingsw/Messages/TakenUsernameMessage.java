package it.polimi.ingsw.Messages;

import java.io.Serializable;

import java.io.Serializable;

/**
 * The TakenUsernameMessage class represents a message indicating that a username is already taken.
 * It implements the Message and Serializable interfaces.
 */
public class TakenUsernameMessage extends Message {
    private final String message = "Username already taken";

    /**
     * Returns a string representation of the message.
     *
     * @return The message.
     */
    @Override
    public String toString() {
        return message;
    }
}
