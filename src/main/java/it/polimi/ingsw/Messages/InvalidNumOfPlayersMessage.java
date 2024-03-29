package it.polimi.ingsw.Messages;

/**
 * The InvalidNumOfPlayersMessage class represents a message indicating an invalid number of players.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class InvalidNumOfPlayersMessage extends Message {
    /**
     * Message to be displayed.
     */
    private final String message = "Invalid number of players";

    /**
     * Default constructor for {@code InvalidNumOfPlayersMessage}.
     */
    public InvalidNumOfPlayersMessage() {
    }

    /**
     * Returns the string {@link InvalidNumOfPlayersMessage#message}.
     *
     * @return The message string.
     */
    @Override
    public String toString(){
        return message;
    }
}