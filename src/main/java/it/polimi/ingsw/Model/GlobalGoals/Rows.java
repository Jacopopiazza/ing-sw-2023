package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;

import java.util.HashSet;

/**
 * The Rows class represents a global goal that requires having a certain number of rows on the shelf
 * with a specific number of different tile colors.
 */
public class Rows extends GlobalGoal {

    private final boolean equal;
    private final int numOfRows;
    private final int differentTilesPerRow;

    /**
     * Constructs a Rows instance with the specified number of players, condition type, number of rows, and different tiles per row.
     *
     * @param people               the number of players in the game
     * @param equal                the condition type, true if the number of different tiles should be equal or less, false if it should be equal or more
     * @param numOfRows            the required number of rows
     * @param differentTilesPerRow the required number of different tiles per row
     * @throws InvalidNumberOfPlayersException if the number of players is invalid
     */
    public Rows(int people, boolean equal, int numOfRows, int differentTilesPerRow) throws InvalidNumberOfPlayersException {
        super(people, equal ? 7 : 6);
        this.equal=equal;
        this.numOfRows=numOfRows;
        this.differentTilesPerRow=differentTilesPerRow;
    }

    /**
     * Checks if the specified shelf satisfies the condition of having the required number of rows
     * with the specified number of different tile colors.
     *
     * @param s the shelf to check
     * @return true if the shelf satisfies the condition, false otherwise
     * @throws MissingShelfException if the shelf is null
     */
    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        HashSet<TileColor> foundColors;
        int count=0;

        for( int i = 0; i < r; i++ ){
            foundColors = new HashSet<TileColor>();
            for( int j = 0; j < c; j++ ){
                if( s.getTile(new Coordinates(i,j)) != null ) foundColors.add(s.getTile(new Coordinates(i,j)).getColor());
            }
            if( equal && foundColors.size() <= differentTilesPerRow ){
                if( ++count == numOfRows ) return true;
            }
            if( !equal && foundColors.size() >= differentTilesPerRow ){
                if( ++count == numOfRows ) return true;
            }
        }
        return false;
    }

}
