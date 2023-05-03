package it.polimi.ingsw.Messages;

public class MissingUsernameMessage implements Message{
    private final String message = "Username is null";

    @Override
    public String toString(){
        return message;
    }
}
