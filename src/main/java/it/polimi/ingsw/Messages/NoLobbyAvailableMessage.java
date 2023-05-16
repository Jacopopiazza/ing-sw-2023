package it.polimi.ingsw.Messages;

import java.io.Serializable;

public class NoLobbyAvailableMessage implements Message, Serializable {
    private final String message = "No lobby available";

    @Override
    public String toString() {
        return message;
    }
}
