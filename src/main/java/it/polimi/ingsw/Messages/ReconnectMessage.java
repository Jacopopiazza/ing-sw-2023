package it.polimi.ingsw.Messages;

public class ReconnectMessage implements Message{
    private String username;
    private Client client;

    public ReconnectMessage(String u, Client client){
        this.username = u;
        this.client = client;
    }

    public String getUsername() {
        return username;
    }

    public Client getClient() {
        return client;
    }
}
