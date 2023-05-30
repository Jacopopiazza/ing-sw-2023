package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;

import java.util.HashSet;

/**
 * The Columns class represents a global goal called "Columns" in the game MyShelfie.
 * This goal checks if a specified number of columns on a shelf meet certain conditions based on the tile colors.
 */
public class Columns extends GlobalGoal {
    private final boolean equal;
    private final int numOfColumns;
    private final int differentTilesPerColumn;

    /**
     * Constructs a Columns global goal with the specified parameters.
     *
     * @param people                 the number of players.
     * @param equal                  a flag indicating if the columns should have an equal or different number of tile colors.
     * @param numOfColumns           the number of columns that should meet the condition.
     * @param differentTilesPerColumn the number of different tile colors required per column (for equal=true) or the minimum number of different tile colors allowed per column (for equal=false).
     * @throws InvalidNumberOfPlayersException if the number of players is invalid.
     */
    public Columns(int people, boolean equal, int numOfColumns, int differentTilesPerColumn) throws InvalidNumberOfPlayersException {
        super(people, equal ? 5 : 2);
        this.equal=equal;
        this.numOfColumns=numOfColumns;
        this.differentTilesPerColumn=differentTilesPerColumn;
    }

    /**
     * Checks if the given shelf satisfies the Columns global goal.
     * The shelf must have the specified number of columns that meet the condition based on the tile colors.
     *
     * @param s the shelf to check.
     * @return true if the shelf satisfies the Columns global goal, false otherwise.
     * @throws MissingShelfException   if the shelf is missing or null.
     * @throws ColumnOutOfBoundsException if the column index is out of bounds.
     */
    @Override
    public boolean check(Shelf s) throws MissingShelfException, ColumnOutOfBoundsException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        HashSet<TileColor> foundColors;
        int count=0;

        for( int j = 0; j < c; j++ ){
            foundColors = new HashSet<TileColor>();
            for( int i = 0; i<r; i++ ){
                if( s.getTile(new Coordinates(i,j)) != null ) foundColors.add(s.getTile(new Coordinates(i,j)).getColor());
            }
            if( equal && foundColors.size() <= differentTilesPerColumn ){
                if( ++count == numOfColumns ) return true;
            }
            if( !equal && foundColors.size() >= differentTilesPerColumn ){
                if( ++count == numOfColumns ) return true;
            }
        }
        return false;
    }

}
