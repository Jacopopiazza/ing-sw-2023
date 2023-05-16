package it.polimi.ingsw.Messages;

import java.io.Serializable;

public class MissingUsernameMessage implements Message, Serializable {
    private final String message = "Username is null";

    @Override
    public String toString(){
        return message;
    }
}
