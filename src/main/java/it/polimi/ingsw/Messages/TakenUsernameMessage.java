package it.polimi.ingsw.Messages;

import java.io.Serializable;

public class TakenUsernameMessage implements Message, Serializable {
    private final String message = "Username already taken";

    @Override
    public String toString(){
        return message;
    }
}
