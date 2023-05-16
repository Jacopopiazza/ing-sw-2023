package it.polimi.ingsw.Messages;

import java.io.Serializable;

public class InvalidActionMessage implements Message, Serializable {
    private final String message = "Invalid turn action";

    @Override
    public String toString() {
        return message;
    }
}
