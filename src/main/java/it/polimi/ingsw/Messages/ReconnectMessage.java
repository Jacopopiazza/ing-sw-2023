package it.polimi.ingsw.Messages;

/**
 * The ReconnectMessage class represents a message indicating a player's intention to reconnect.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class ReconnectMessage extends Message {

    /**
     * Username of the player to be reconnected.
     */
    private final String username;

    /**
     * Constructs a new {@code ReconnectMessage} object with the specified username.
     *
     * @param username The username of the player reconnecting.
     */
    public ReconnectMessage(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the player who is trying to reconnect.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }
}
