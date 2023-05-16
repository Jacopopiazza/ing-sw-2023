package it.polimi.ingsw.Messages;

import java.io.Serializable;

public class NoUsernameToReconnectMessage implements Message, Serializable {
    private final String message = "No Username to reconnect";

    @Override
    public String toString(){
        return message;
    }
}
