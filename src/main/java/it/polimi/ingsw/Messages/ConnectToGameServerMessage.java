package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Network.Server;

import java.io.Serializable;

public class ConnectToGameServerMessage implements Message, Serializable {
    private Server server;

    public ConnectToGameServerMessage(Server server) {
        this.server = server;
    }

    public Server getServer(){
        return this.server;
    }
}
