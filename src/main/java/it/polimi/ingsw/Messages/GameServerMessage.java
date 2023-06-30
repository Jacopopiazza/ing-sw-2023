package it.polimi.ingsw.Messages;
import it.polimi.ingsw.Network.Server;

/**
 * The GameServerMessage class represents a message telling the Client that the server it should communicate with has changed.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class GameServerMessage extends Message {
    /**
     * Reference to the new server to replace the old server with.
     */
    private final Server server;

    /**
     * Constructs a {@code GameServerMessage} with the specified server.
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
