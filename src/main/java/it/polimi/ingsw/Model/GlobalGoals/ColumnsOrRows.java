package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;

import java.util.HashSet;

/**
 * The ColumnsOrRows class represents the global goals related to Columns and Rows in the game MyShelfie.
 * This goal checks if a specified number of columns/rows on a shelf meet certain conditions based on the tile colors.
 */
public class ColumnsOrRows extends GlobalGoal {
    private final boolean equal;
    // checkColumn is True if this is the global goal related to columns, otherwise is the one related to rows
    private final boolean checkColumns;
    private final int numOfLines;
    private final int differentTilesPerLine;

    private static int myId(boolean eq, boolean cc){
        return ( cc ? (eq ? 5 : 2) : (eq ? 7 : 6)) ;
    }

    /**
     * Constructs a ColumnsOrRows global goal with the specified parameters.
     *
     * @param p                  the number of players.
     * @param eq                   a flag indicating if the columns/rows should have an equal or different number of tile colors.
     * @param cc            a flag indicating whether this global goal is related to columns or to rows
     * @param num              the number of columns/rows that should meet the condition.
     * @param diff the number of different tile colors required per column/row (for equal=true) or the minimum number of different tile colors allowed per column/row (for equal=false).
     * @throws InvalidNumberOfPlayersException if the number of players is invalid.
     */
    public ColumnsOrRows(int p, boolean eq, boolean cc, int num, int diff) throws InvalidNumberOfPlayersException {
        super(p, myId(eq, cc));
        this.equal = eq;
        this.checkColumns = cc;
        this.numOfLines = num;
        this.differentTilesPerLine = diff;
        if( cc ){
            if( eq )
                this.description = "Three columns each formed by 6 tiles Five tiles of the same type forming an X. " +
                    "of maximum three different types. One " +
                    "column can show the same or a different " +
                    "combination of another column.";
            else
                this.description = "Two columns each formed by 6 " +
                    "different types of tiles.";
        }
        else{
            if( eq )
                this.description = "Four lines each formed by 5 tiles of " +
                    "maximum three different types. One " +
                    "line can show the same or a different " +
                    "combination of another line.";
            else
                this.description = "Two lines each formed by 5 different " +
                    "types of tiles. One line can show the " +
                    "same or a different combination of the " +
                    "other line.";
        }
    }

    /**
     * Checks if the given shelf satisfies the Columns/Rows global goal.
     * The shelf must have the specified number of columns/rows that meet the condition based on the tile colors.
     *
     * @param s the shelf to check.
     * @return true if the shelf satisfies the Columns/Rows global goal, false otherwise.
     * @throws MissingShelfException   if the shelf is missing or null.
     */
    @Override
    public boolean check(Shelf s) throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int outerBound;
        int innerBound;
        if(checkColumns){
            outerBound = Shelf.getColumns();
            innerBound = Shelf.getRows();
        }
        else{
            outerBound = Shelf.getRows();
            innerBound= Shelf.getColumns();
        }
        HashSet<TileColor> foundColors;
        int count=0;
        boolean isFull;
        Tile currentTile;

        for( int i = 0; i < outerBound; i++ ){
            foundColors = new HashSet<TileColor>();
            isFull = true;
            for( int j = 0; j<innerBound && isFull; j++ ){
                if(checkColumns) currentTile = s.getTile(new Coordinates(j,i));
                else currentTile = s.getTile(new Coordinates(i,j));

                if( currentTile != null ) foundColors.add(currentTile.getColor());
                else isFull = false;
            }
            if(isFull){
                if( equal && foundColors.size() <= differentTilesPerLine){
                    if( ++count == numOfLines) return true;
                }
                if( !equal && foundColors.size() >= differentTilesPerLine){
                    if( ++count == numOfLines) return true;
                }
            }
        }
        return false;
    }

}
