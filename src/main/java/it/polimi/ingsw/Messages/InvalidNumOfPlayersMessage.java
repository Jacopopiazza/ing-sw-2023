package it.polimi.ingsw.Messages;

/**
 * The InvalidNumOfPlayersMessage class represents a message indicating an invalid number of players.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class InvalidNumOfPlayersMessage extends Message {
    private final String message = "Invalid number of players";

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