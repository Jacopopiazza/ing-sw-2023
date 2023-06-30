package it.polimi.ingsw.Messages;


/**
 * The TakenUsernameMessage class represents a message indicating that a username is already taken.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class TakenUsernameMessage extends Message {

    /**
     * Message to be displayed.
     */
    private final String message = "Username already taken";

    /**
     * Default constructor for {@code TakenUsernameMessage}.
     */
    public TakenUsernameMessage() {
    }

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
