package it.polimi.ingsw.Messages;

import java.io.Serializable;

public class DisconnectMessage implements Message, Serializable {
    private String username;
    public DisconnectMessage(String username) {
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}