package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Network.Client;

import java.io.Serializable;


import java.io.Serializable;

/**
 * The ReconnectMessage class represents a message indicating a player's intention to reconnect.
 * It implements the Message interface and is serializable.
 */
public class ReconnectMessage implements Message, Serializable {
    private String username;

    /**
     * Constructs a ReconnectMessage object with the specified username.
     *
     * @param username The username of the player reconnecting.
     */
    public ReconnectMessage(String username) {
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
