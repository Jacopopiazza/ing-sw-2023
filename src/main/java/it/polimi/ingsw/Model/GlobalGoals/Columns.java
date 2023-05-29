package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;

import java.util.HashSet;

public class Columns extends GlobalGoal {
    private final boolean equal;
    private final int numOfColumns;
    private final int differentTilesPerColumn;

    public Columns(int people, boolean equal, int numOfColumns, int differentTilesPerColumn) throws InvalidNumberOfPlayersException {
        super(people, equal ? 5 : 2);
        this.equal=equal;
        this.numOfColumns=numOfColumns;
        this.differentTilesPerColumn=differentTilesPerColumn;
    }

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
