package it.polimi.ingsw.Messages;

import java.io.Serializable;
import java.util.List;

import java.io.Serializable;
import java.util.List;

/**
 * The LobbyMessage class represents a message containing the list of players in the lobby.
 * It implements the Message interface and is serializable.
 */
public class LobbyMessage extends Message {

    private List<String> players;

    /**
     * Constructs a LobbyMessage object with the specified list of players.
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
