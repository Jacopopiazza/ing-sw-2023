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

    /**
     * Constructs a ColumnsOrRows global goal with the specified parameters.
     *
     * @param people                  the number of players.
     * @param equal                   a flag indicating if the columns/rows should have an equal or different number of tile colors.
     * @param checkColumns            a flag indicating whether this global goal is related to columns or to rows
     * @param numOfLines              the number of columns/rows that should meet the condition.
     * @param differentTilesPerLine the number of different tile colors required per column/row (for equal=true) or the minimum number of different tile colors allowed per column/row (for equal=false).
     * @throws InvalidNumberOfPlayersException if the number of players is invalid.
     */
    public ColumnsOrRows(int people, boolean equal, boolean checkColumns, int numOfLines, int differentTilesPerLine) throws InvalidNumberOfPlayersException {
        super(people, checkColumns? (equal ? 5 : 2) : (equal ? 7 : 6));
        this.equal=equal;
        this.checkColumns = checkColumns;
        this.numOfLines = numOfLines;
        this.differentTilesPerLine =differentTilesPerLine;
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
