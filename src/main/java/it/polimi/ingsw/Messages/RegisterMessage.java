package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Network.Client;

import java.io.Serializable;

public class RegisterMessage implements Message, Serializable {
    private String username;
    private Client client;
    private int numOfPlayers;

    public RegisterMessage(String u, Client client, int numOfPlayers) {
        this.username = u;
        this.client = client;
        this.numOfPlayers = numOfPlayers;
    }

    public String getUsername() {
        return username;
    }

    public Client getClient() {
        return client;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
