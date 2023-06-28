package it.polimi.ingsw.Messages;

import java.io.Serializable;

import java.io.Serializable;

/**
 * The TakenUsernameMessage class represents a message indicating that a username is already taken.
 * It extends the Message abstract class and is serializable.
 */
public class TakenUsernameMessage extends Message {
    private final String message = "Username already taken";

    /**
     * Returns the string {@link TakenUsernameMessage#message}.
     *
     * @return The message.
     */
    @Override
    public String toString() {
        return message;
    }
}
