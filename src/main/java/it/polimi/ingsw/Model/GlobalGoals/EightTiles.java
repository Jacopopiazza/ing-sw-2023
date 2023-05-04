package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.TileColor;

public class EightTiles extends GlobalGoal {
    public EightTiles(int people) throws InvalidNumberOfPlayersException {
        super(people, "EightTiles");
    }

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int numOfEqualTiles = 8;
        int[] counters = new int[TileColor.values().length];

        for( int i = 0; i < r; i++ ){
            for( int j = 0; j < c; j++ ){
                Coordinates coord = new Coordinates(i, j);
                //if not null, counting one more tile of its color
                if( s.getTile(coord) != null ){
                    counters[s.getTile(coord).getColor().ordinal()]++;
                    if( counters[ s.getTile(coord).getColor().ordinal() ] >= numOfEqualTiles )
                        return true;
                }

            }
        }
        return false;
    }

}
