package it.polimi.ingsw.Messages;

import java.io.Serializable;

/**
 * The ReconnectMessageTicket class represents a message ticket for reconnecting.
 * It implements the Message interface and is serializable.
 */
public class ReconnectMessageTicket extends Message {
    private String username;

    /**
     * Constructs a ReconnectMessageTicket object with the specified username.
     *
     * @param username The username of the player reconnecting.
     */
    public ReconnectMessageTicket(String username) {
        this.username = username;
    }

    /**
     * Returns the username of the player reconnecting.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }
}
