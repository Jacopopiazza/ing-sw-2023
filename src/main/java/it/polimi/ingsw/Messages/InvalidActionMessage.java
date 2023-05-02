package it.polimi.ingsw.Messages;

public class InvalidActionMessage implements Message{
    private final String message = "Invalid turn action";

    @Override
    public String toString(){
        return message;
    }
}
