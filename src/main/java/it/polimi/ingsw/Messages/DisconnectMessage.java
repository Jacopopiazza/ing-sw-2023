package it.polimi.ingsw.Messages;
import java.io.Serializable;

/**
 * The DisconnectMessage class represents a message indicating that a player has disconnected from the game.
 * It extends the Message abstract class and is serializable.
 */
public class DisconnectMessage extends Message {
    private String username;

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
