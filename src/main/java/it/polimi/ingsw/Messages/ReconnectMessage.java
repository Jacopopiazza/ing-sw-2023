package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Network.Client;

import java.io.Serializable;

public class ReconnectMessage implements Message, Serializable {
    private String username;
    private Client client;

    public ReconnectMessage(String u, Client client) {
        this.username = u;
        this.client = client;
    }

    public String getUsername() {
        return username;
    }

    public Client getClient() {
        return client;
    }
}
