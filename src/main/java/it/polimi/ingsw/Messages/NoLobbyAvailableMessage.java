package it.polimi.ingsw.Messages;


/**
 * The NoLobbyAvailableMessage class represents a message indicating that no lobby is available.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class NoLobbyAvailableMessage extends Message {
    private final String message = "No lobby available";

    /**
     * Returns the string {@link NoLobbyAvailableMessage#message}.
     *
     * @return The message string.
     */
    @Override
    public String toString() {
        return message;
    }
}
