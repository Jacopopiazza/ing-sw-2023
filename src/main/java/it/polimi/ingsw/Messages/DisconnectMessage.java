package it.polimi.ingsw.Messages;

public class DisconnectMessage implements Message{
    private String username;
    public DisconnectMessage(String username){
        this.username = username;
    }
    public String getUsername() {
        return username;
    }
}