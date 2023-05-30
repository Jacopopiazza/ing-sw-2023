package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Model.Coordinates;

import java.io.Serializable;

/**
 * The TurnActionMessage class represents a message containing the details of a turn action.
 * It implements the Message and Serializable interfaces.
 */
public class TurnActionMessage implements Message, Serializable {
    private String username;
    private Coordinates[] chosenTiles;
    private int column;

    /**
     * Constructs a TurnActionMessage object with the specified parameters.
     *
     * @param u     The username of the player performing the turn action.
     * @param chosenTiles  The array of coordinates representing the chosen tiles.
     * @param column       The column where the chosen tiles will be inserted.
     */
    public TurnActionMessage(String u, Coordinates[] chosenTiles, int column) {
        this.username = u;
        this.chosenTiles = chosenTiles;
        this.column = column;
    }

    /**
     * Returns the username of the player.
     *
     * @return The username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Returns the array of coordinates representing the chosen tiles.
     *
     * @return The chosen tiles coordinates.
     */
    public Coordinates[] getChosenTiles() {
        return chosenTiles;
    }

    /**
     * Returns the column where the chosen tiles will be inserted.
     *
     * @return The column index.
     */
    public int getColumn() {
        return column;
    }


}
