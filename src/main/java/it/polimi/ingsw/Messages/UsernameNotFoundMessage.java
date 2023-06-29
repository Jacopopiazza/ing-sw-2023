package it.polimi.ingsw.Messages;

/**
 * The UsernameNotFoundMessage class represents a message indicating that there is no username to reconnect.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class UsernameNotFoundMessage extends Message {

    /**
     * Message to be displayed.
     */
    private final String message = "No Username to reconnect";

    /**
     * Default constructor of {@code UsernameNotFoundMessage}.
     */
    public UsernameNotFoundMessage() {
    }

    /**
     * Returns the string {@link UsernameNotFoundMessage#message}.
     *
     * @return The message string.
     */
    @Override
    public String toString() {
        return message;
    }
}
