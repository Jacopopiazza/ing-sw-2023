package it.polimi.ingsw.Messages;

import java.util.List;

/**
 * The LobbyMessage class represents a message containing the list of players in the lobby.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class LobbyMessage extends Message {

    /**
     * Usernames which are currently in the lobby.
     */
    private final List<String> players;

    /**
     * Constructs a new {@code LobbyMessage} with the specified list of players.
     *
     * @param players The list of players in the lobby.
     */
    public LobbyMessage(List<String> players) {
        this.players = players;
    }

    /**
     * Returns the list of players in the lobby.
     *
     * @return The list of players.
     */
    public List<String> getPlayers() {
        return players;
    }
}
