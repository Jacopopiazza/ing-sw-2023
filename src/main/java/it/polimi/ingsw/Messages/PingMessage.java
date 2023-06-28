package it.polimi.ingsw.Messages;

/**
 * Represents a ping message sent to test the connection with a player.
 * This kind of messages contains a ping number to identify the player's client who is answering.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class PingMessage extends Message {
    private final int pingNumber;

    /**
     * Constructs a new {@code PingMessage} with the specified pingNumber.
     *
     * @param pingNumber the message
     */
    public PingMessage(int pingNumber) {
        this.pingNumber = pingNumber;
    }

    /**
     * Returns the pingNumber.
     *
     * @return the pingNumber
     */
    public int getPingNumber() {
        return this.pingNumber;
    }
}
