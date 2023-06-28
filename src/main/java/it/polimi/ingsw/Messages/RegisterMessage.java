package it.polimi.ingsw.Messages;

/**
 * The RegisterMessage class represents a message for registering a player.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class RegisterMessage extends Message {
    private final String username;
    private final int numOfPlayers;

    /**
     * Constructs a new {@code RegisterMessage} object with the specified username and number of players.
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
