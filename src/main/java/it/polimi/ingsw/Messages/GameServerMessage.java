package it.polimi.ingsw.Messages;
import it.polimi.ingsw.Network.Server;

/**
 * The GameServerMessage class represents a message containing the game server information.
 * It implements the Message interface.
 */
public class GameServerMessage extends Message {
    private Server server;

    /**
     * Constructs a GameServerMessage with the specified server.
     *
     * @param server the game server
     */
    public GameServerMessage(Server server) {
        this.server = server;
    }

    /**
     * Returns the game server.
     *
     * @return the game server
     */
    public Server getServer() {
        return this.server;
    }
}
