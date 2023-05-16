package it.polimi.ingsw.Messages;

import java.io.Serializable;

public class InvalidNumOfPlayersMessage implements Message, Serializable {
    private final String message = "Invalid number of players";

    @Override
    public String toString(){
        return message;
    }
}
