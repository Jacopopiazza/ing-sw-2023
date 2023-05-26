package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Network.Client;

import java.io.Serializable;


public class ReconnectMessage implements Message, Serializable {
    private String username;

    public ReconnectMessage(String u) {
        this.username = u;
    }

    public String getUsername() {
        return username;
    }

}
