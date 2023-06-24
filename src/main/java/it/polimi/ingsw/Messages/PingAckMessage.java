package it.polimi.ingsw.Messages;

public class PingAckMessage extends Message{
    private final Integer pingNumber;

    /**
     * Constructs a PingMessage with the specified message.
     *
     * @param pingNumber the message
     */
    public PingAckMessage(Integer pingNumber) {
        this.pingNumber = pingNumber;
    }

    public PingAckMessage(PingMessage pingMessage) {
        this.pingNumber = pingMessage.getpingNumber();
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
