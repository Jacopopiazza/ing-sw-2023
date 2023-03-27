package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;

import java.util.HashSet;


public class EqualColumns extends GlobalGoal {


    public EqualColumns(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    @Override
    public boolean check(Shelf s) throws MissingShelfException, ColumnOutOfBoundsException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int numOfEqualColumns = 3;
        int numOfDifferentTilesPerColumn = 3;
        HashSet<TileColor> foundColors;
        int count=0;

        for( int j = 0; j<c; j++ ){
            foundColors = new HashSet<TileColor>();
            for( int i = 0; i<r; i++ ){
                if( s.getTile(new Coordinates(i,j)) != null ) foundColors.add( s.getTile(new Coordinates(i,j)).getColor() );
            }
            if( foundColors.size() <= numOfDifferentTilesPerColumn ){
                if( ++count == numOfEqualColumns ) return true;
            }
        }
        return false;
    }
}
