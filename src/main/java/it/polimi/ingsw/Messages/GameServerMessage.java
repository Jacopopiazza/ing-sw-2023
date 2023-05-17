package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Network.Server;

public class GameServerMessage implements Message {
    private Server server;

    public GameServerMessage(Server server) {
        this.server = server;
    }

    public Server getServer() {
        return this.server;
    }
}
