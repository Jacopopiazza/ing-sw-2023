package it.polimi.ingsw.Messages;

public class ReconnectMessage implements Message{
    private String nickname;
    private Client client;

    public ReconnectMessage(String nickname, Client client){
        this.nickname = nickname;
        this.client = client;
    }

    public String getNickname() {
        return nickname;
    }

    public Client getClient() {
        return client;
    }
}
