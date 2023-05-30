package it.polimi.ingsw.Messages;

import java.io.Serializable;

/**
 * The RegisterMessageTicket class represents a ticket message for registering a player.
 * It implements the Message and Serializable interfaces.
 */
public class RegisterMessageTicket extends Message {
    private String username;
    private int numOfPlayers;

    /**
     * Constructs a RegisterMessageTicket object with the specified username and number of players.
     *
     * @param username     The username of the player to register.
     * @param numOfPlayers The number of players for the game.
     */
    public RegisterMessageTicket(String username, int numOfPlayers) {
        this.username = username;
        this.numOfPlayers = numOfPlayers;
    }

    /**
     * Returns the username of the player to register.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the number of players for the game.
     *
     * @return The number of players.
     */
    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
