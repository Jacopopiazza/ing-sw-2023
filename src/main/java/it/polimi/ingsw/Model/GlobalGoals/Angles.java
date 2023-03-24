package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;


public class Angles extends GlobalGoal {

    public Angles(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        if( s == null ){
            throw new MissingShelfException();
        }

        if( ( s.getTile(new Coordinates(0,0)) == null )
                || ( s.getTile(new Coordinates(0,c-1)) == null )
                || ( s.getTile(new Coordinates(r-1,0)) == null )
                || ( s.getTile(new Coordinates(r-1,c-1)) == null )
        ) return false;

        if( s.getTile(new Coordinates(0,0)).getColor().equals(s.getTile(new Coordinates(0,c-1)).getColor())
                && s.getTile(new Coordinates(0,c-1)).getColor().equals(s.getTile(new Coordinates(r-1,0)).getColor())
                && s.getTile(new Coordinates(r-1,0)).getColor().equals(s.getTile(new Coordinates(r-1,c-1)).getColor())
        ) return true;

        return false;
    }
}
