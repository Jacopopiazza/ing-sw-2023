package it.polimi.ingsw.Model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents the coordinates of a position on a game board.
 */
public class Coordinates implements Serializable {
    private final int ROW;
    private final int COL;

    /**
     * Constructs a new {@code Coordinates} object with the specified row and column values.
     *
     * @param r The row value.
     * @param c The column value.
     */
    public Coordinates(int r, int c) {
        this.ROW = r;
        this.COL = c;
    }

    /**
     * Returns the row value of the coordinates.
     *
     * @return The row value.
     */
    public int getROW() {
        return ROW;
    }

    /**
     * Returns the column value of the coordinates.
     *
     * @return The column value.
     */
    public int getCOL() {
        return COL;
    }

    /**
     * Compares this Coordinates object to the specified object. The result is true if and only if
     * the argument is not null and is a Coordinates object that has the same row and column values
     * as this object.
     *
     * @param o The object to compare.
     * @return True if the objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object o) {
        if( this == o )
            return true;
        if( ( o == null ) || ( this.getClass() != o.getClass() ) )
            return false;
        Coordinates temp = (Coordinates) o;
        return ( ROW == temp.ROW ) && ( COL == temp.COL );
    }

    /**
     * Returns the hash code value for this Coordinates object.
     *
     * @return The hash code value.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ROW, COL);
    }

    /**
     * Creates and returns a copy of this Coordinates object.
     *
     * @return A new Coordinates object with the same row and column values.
     */
    public Coordinates clone() {
        return new Coordinates(this.ROW,this.COL);
    }

}