package it.polimi.ingsw.Messages;

public class PingMessage extends Message {
    private final Integer pingNumber;

    /**
     * Constructs a PingMessage with the specified message.
     *
     * @param pingNumber the message
     */
    public PingMessage(Integer pingNumber) {
        this.pingNumber = pingNumber;
    }


    /**
     * Returns the pingNumber.
     *
     * @return the pingNumber
     */
    public Integer getpingNumber() {
        return this.pingNumber;
    }
}
