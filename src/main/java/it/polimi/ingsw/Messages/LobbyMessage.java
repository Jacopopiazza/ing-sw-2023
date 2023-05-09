package it.polimi.ingsw.Messages;

import java.util.ArrayList;
import java.util.List;

public class LobbyMessage implements Message {

    private List<String> players;

    public LobbyMessage(List<String> players){
        this.players = players;
    }

    public List<String> getPlayers() {
        return players;
    }
}
