package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.Model.Coordinates;


import java.util.HashSet;

public class DifferentColumns extends GlobalGoal {

    // requires allowed configuration of Shelf s
    @Override
    public boolean check( Shelf s )  throws MissingShelfException {
        HashSet<TileColor> foundColors;
        int count;
        boolean goOn;

        if( s == null ) throw new MissingShelfException();

        count = 0;
        for( int j = 0; j<s.getColumns(); j++ ){
            foundColors = new HashSet<TileColor>();
            goOn = true;
            for( int i = 0; i<s.getRows() && goOn; i++ ){
                Coordinates c = new Coordinates(i,j);
                if( s.getTile(c) == null ) goOn = false;
                else if( foundColors.contains( s.getTile(c).getColor()) ) goOn = false;
                else foundColors.add( s.getTile(c).getColor() );
            }
            if( foundColors.size() == s.getRows() ){
                if( ++count == 2 ) return true;
            }
        }
        return false;
    }
}

