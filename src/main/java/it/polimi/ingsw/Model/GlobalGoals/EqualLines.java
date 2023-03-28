package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;

import java.util.HashSet;

public class EqualLines extends GlobalGoal {
    public EqualLines(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int numOfEqualRows = 4;
        int differentTilesPerRow = 3;
        HashSet<TileColor> foundColors;
        int count=0;

        for( int i = 0; i < r; i++ ){
            foundColors = new HashSet<TileColor>();
            for( int j = 0; j < c; j++ ){
                if( s.getTile(new Coordinates(i,j)) != null ) foundColors.add(s.getTile(new Coordinates(i,j)).getColor());
            }
            if( foundColors.size() <= differentTilesPerRow ){
                if( ++count == numOfEqualRows ) return true;
            }
        }
        return false;
    }

}
