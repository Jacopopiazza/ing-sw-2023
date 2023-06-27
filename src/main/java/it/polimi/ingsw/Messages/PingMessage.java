package it.polimi.ingsw.Messages;

public class PingMessage extends Message {
    private final int pingNumber;
    private final String sender;

    /**
     * Constructs a PingMessage with the specified message.
     *
     * @param pingNumber the message
     */
    public PingMessage(int pingNumber) {
        this.pingNumber = pingNumber;
        this.sender = "server";
    }

    public PingMessage(int pingNumber, String sender) {
        this.pingNumber = pingNumber;
        this.sender = sender;
    }

    public String getSender() {
        return sender;
    }

    /**
     * Returns the pingNumber.
     *
     * @return the pingNumber
     */
    public int getpingNumber() {
        return this.pingNumber;
    }
}
