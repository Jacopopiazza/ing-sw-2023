package it.polimi.ingsw.Messages;
import java.io.Serializable;

/**
 * The DisconnectMessage class represents a message indicating that a player has disconnected from the game.
 * It implements the Message interface and is serializable.
 */
public class DisconnectMessage implements Message, Serializable {
    private String username;

    /**
     * Constructs a DisconnectMessage with the specified username.
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
