package it.polimi.ingsw.Messages;

public class NoUsernameToReconnectMessage implements Message{
    private final String message = "No Username to reconnect";

    @Override
    public String toString(){
        return message;
    }
}
