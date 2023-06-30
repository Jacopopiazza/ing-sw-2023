package it.polimi.ingsw.Messages;

/**
 * The DisconnectMessage class represents a message indicating that a player has disconnected from the game. It is actually only used to kick a player from the lobby.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class DisconnectMessage extends Message {
    /**
     * Username of the player to be disconnected.
     */
    private final String username;

    /**
     * Constructs a {@code DisconnectMessage} with the specified username.
     *
     * @param username the username of the player who has disconnected
     */
    public DisconnectMessage(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the player who has disconnected.
     *
     * @return the username of the player
     */
    public String getUsername() {
        return username;
    }
}
