package it.polimi.ingsw.Messages;

public class NoLobbyAvailableMessage implements Message {
    private final String message = "No lobby available";

    @Override
    public String toString(){
        return message;
    }
}
