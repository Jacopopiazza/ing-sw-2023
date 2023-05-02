package it.polimi.ingsw.Messages;

public class RegisterMessage implements Message{
    private String nickname;
    private Client client;
    private int numOfPlayers;

    public RegisterMessage(String nickname, Client client, int numOfPlayers){
        this.nickname = nickname;
        this.client = client;
        this.numOfPlayers = numOfPlayers;
    }

    public String getNickname() {
        return nickname;
    }

    public Client getClient() {
        return client;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }
}
