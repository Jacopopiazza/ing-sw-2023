package it.polimi.ingsw.Messages;

public class PingMessage extends Message {
    private final int pingNumber;

    /**
     * Constructs a PingMessage with the specified message.
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
    public int getpingNumber() {
        return this.pingNumber;
    }
}
