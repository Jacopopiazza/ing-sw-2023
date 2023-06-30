package it.polimi.ingsw.Messages;


/**
 * The NoLobbyAvailableMessage class represents a message indicating that no lobby is available.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class NoLobbyAvailableMessage extends Message {

    /**
     * Message to be displayed.
     */
    private final String message = "No lobby available";

    /**
     * Default constructor for {@code NoLobbyAvailableMessage}.
     */
    public NoLobbyAvailableMessage() {
    }

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
