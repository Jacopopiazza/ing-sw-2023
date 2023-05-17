package it.polimi.ingsw.Messages;

import java.io.Serializable;

public class ReconnectMessageTicket implements Message, Serializable {
    private String username;

    public ReconnectMessageTicket(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
