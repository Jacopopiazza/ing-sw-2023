package it.polimi.ingsw.Messages;

/**
 * The UsernameNotFoundMessage class represents a message indicating that there is no username to reconnect.
 * It extends the Message abstract class and is serializable.
 */
public class UsernameNotFoundMessage extends Message {
    private final String message = "No Username to reconnect";

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
