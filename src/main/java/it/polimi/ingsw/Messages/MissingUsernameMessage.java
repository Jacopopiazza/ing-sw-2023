package it.polimi.ingsw.Messages;

/**
 * The MissingUsernameMessage class represents a message indicating that the username is missing or null.
 * It extends the Message abstract class and is serializable.
 */
public class MissingUsernameMessage extends Message {
    private final String message = "Username is null";

    /**
     * Returns the string {@link MissingUsernameMessage#message}.
     *
     * @return The message string.
     */
    @Override
    public String toString(){
        return message;
    }
}

