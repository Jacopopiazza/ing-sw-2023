package it.polimi.ingsw.Messages;

import java.io.Serializable;

public class RegisterMessageTicket implements Message, Serializable {
    private String username;
    private int numOfPlayers;

    public RegisterMessageTicket(String username, int numOfPlayers) {
        this.username = username;
        this.numOfPlayers = numOfPlayers;
    }

    public String getUsername() {
        return username;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
