package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Network.Client;

public class ReconnectMessage implements Message{
    private String username;
    private Client client;

    public ReconnectMessage(String u, Client client){
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
