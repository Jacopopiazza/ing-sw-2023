package it.polimi.ingsw.Messages;

public class InvalidNumOfPlayersMessage implements Message{
    private final String message = "Invalid number of players";

    @Override
    public String toString(){
        return message;
    }
}
