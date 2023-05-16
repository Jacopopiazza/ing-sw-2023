package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Network.Server;

public class ConnectToGameServerMessage implements Message{
    private Server server;

    public ConnectToGameServerMessage(Server server) {
        this.server = server;
    }

    public Server getServer(){ return this.server; }
}
