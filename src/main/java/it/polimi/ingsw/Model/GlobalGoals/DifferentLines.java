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
        HashSet<TileColor> foundColors;
        int count;
        boolean goOn;

        if( s == null ){
            throw new MissingShelfException();
        }

        count = 0;
        for( int i = 0; i<Shelf.getRows(); i++ ){
            goOn = true;
            foundColors = new HashSet<TileColor>();
            for( int j = 0; j<Shelf.getColumns() && goOn; j++ ){
                Coordinates c = new Coordinates(i,j);
                if( s.getTile(c) == null ) goOn = false;
                else if( foundColors.contains( s.getTile(c).getColor()) ) goOn = false;
                else foundColors.add( s.getTile(c).getColor() );
            }
            if( foundColors.size() == Shelf.getColumns() ){
                if( ++count == 2 ) return true;
            }
        }
        return false;
    }
}
