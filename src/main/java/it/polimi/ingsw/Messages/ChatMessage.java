package it.polimi.ingsw.Messages;

/**
 * The ChatMessage class represents a chat message sent between players.
 * It implements the Message interface.
 */
public class ChatMessage extends Message{

    private String sender;

    private String recipient;

    private String message;

    /**
     * Constructs a new ChatMessage with the specified sender and message.
     *
     * @param sender the sender of the message
     * @param message the message
     */
    public ChatMessage(String sender, String message) {
        this(sender,message,null);
    }

    /**
     * Constructs a new ChatMessage with the specified sender, recipient and message.
     *
     * @param sender the sender of the message
     * @param recipient the recipient of the message
     * @param message the message
     */
    public ChatMessage(String sender, String message,String recipient) {
        this.sender = sender;
        this.recipient = recipient;
        this.message = message;
    }

    /**
     * Returns the sender username.
     *
     * @return the sender username
     */
    public String getSender() {
        return sender;
    }

    /**
     * Returns the recipient of the message.
     *
     * @return the recipient of the message
     */
    public String getRecipient() {
        return recipient;
    }
}
