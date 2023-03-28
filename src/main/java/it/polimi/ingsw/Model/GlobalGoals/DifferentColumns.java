package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.Model.Coordinates;

import java.util.HashSet;

public class DifferentColumns extends GlobalGoal {
    public DifferentColumns(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    // requires allowed configuration of Shelf s
    @Override
    public boolean check( Shelf s )  throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int numOfDifferentColumns = 2;
        int numOfDifferentTilesPerColumn = Shelf.getRows();
        HashSet<TileColor> foundColors;
        int count=0;

        for( int j = 0; j<c; j++ ){
            foundColors = new HashSet<TileColor>();
            for( int i = 0; i < r; i++ ){
                if( s.getTile(new Coordinates(i,j)) != null ) foundColors.add(s.getTile(new Coordinates(i,j)).getColor());
            }
            if( foundColors.size() == numOfDifferentTilesPerColumn ){
                if( ++count == numOfDifferentColumns ) return true;
            }
        }
        return false;
    }

}

