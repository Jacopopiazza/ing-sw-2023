package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.TileColor;


public class EightTiles extends GlobalGoal {

    public EightTiles(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        int[] counters = new int[TileColor.values().length];

        if(s==null){
            throw new MissingShelfException();
        }

        for( int i=0; i<Shelf.getRows(); i++ ){
            for( int j=0; j<Shelf.getColumns(); j++ ){
                Coordinates c = new Coordinates(i,j);
                //if not null, counting one more tile of its color
                if( s.getTile(c) != null ) counters[ s.getTile(c).getColor().ordinal() ]++;
                if( counters[ s.getTile(c).getColor().ordinal() ] >= 8 ) return true;
            }
        }
        return false;
    }
}
