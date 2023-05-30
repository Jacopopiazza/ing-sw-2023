package it.polimi.ingsw.Messages;

import java.io.Serializable;
/**
 * The InvalidNumOfPlayersMessage class represents a message indicating an invalid number of players.
 * It implements the Message interface and is serializable.
 */
public class InvalidNumOfPlayersMessage implements Message, Serializable {
    private final String message = "Invalid number of players";

    /**
     * Returns the string representation of the invalid number of players message.
     *
     * @return The message string.
     */
    @Override
    public String toString(){
        return message;
    }
}