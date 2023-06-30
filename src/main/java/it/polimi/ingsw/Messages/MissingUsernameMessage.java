package it.polimi.ingsw.Messages;

/**
 * The MissingUsernameMessage class represents a message indicating that the username is missing or null.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class MissingUsernameMessage extends Message {

    /**
     * Message to be displayed.
     */
    private final String message = "Username is null";

    /**
     * Default constructor for {@code MissingUsernameMessage}.
     */
    public MissingUsernameMessage() {
    }

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

