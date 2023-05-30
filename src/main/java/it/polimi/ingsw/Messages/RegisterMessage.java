package it.polimi.ingsw.Messages;

/**
 * The RegisterMessage class represents a message for registering a player.
 * It implements the Message interface.
 */
public class RegisterMessage extends Message {
    private String username;
    private int numOfPlayers;

    /**
     * Constructs a RegisterMessage object with the specified username and number of players.
     *
     * @param username     The username of the player to register.
     * @param numOfPlayers The number of players for the game.
     */
    public RegisterMessage(String username, int numOfPlayers) {
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
