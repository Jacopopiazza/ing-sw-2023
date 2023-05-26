package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Network.Client;

public class RegisterMessage implements Message {
    private String username;
    private int numOfPlayers;

    public RegisterMessage(String u, int numOfPlayers) {
        this.username = u;
        this.numOfPlayers = numOfPlayers;
    }

    public String getUsername() {
        return username;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
