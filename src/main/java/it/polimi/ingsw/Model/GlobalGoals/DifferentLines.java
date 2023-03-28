package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.TileColor;

import java.util.HashSet;

public class DifferentLines extends GlobalGoal {
    public DifferentLines(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int numOfDifferentRows = 2;
        int numOfDifferentTilesPerRow = Shelf.getColumns();
        HashSet<TileColor> foundColors;
        int count = 0;

        for( int i = 0; i < r; i++ ){
            foundColors = new HashSet<TileColor>();
            for( int j = 0; j < c; j++ ){
                if( s.getTile(new Coordinates(i,j)) != null ) foundColors.add( s.getTile(new Coordinates(i,j)).getColor() );
            }
            if( foundColors.size() == numOfDifferentTilesPerRow ){
                if( ++count == numOfDifferentRows ) return true;
            }
        }
        return false;
    }

}
