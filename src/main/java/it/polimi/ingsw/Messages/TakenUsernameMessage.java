package it.polimi.ingsw.Messages;

public class TakenUsernameMessage implements Message{
    private final String message = "Username already taken";

    @Override
    public String toString(){
        return message;
    }
}
