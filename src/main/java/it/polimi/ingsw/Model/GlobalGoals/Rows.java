package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;

import java.util.HashSet;

public class Rows extends GlobalGoal {

    private final boolean equal;
    private final int numOfRows;
    private final int differentTilesPerRow;

    public Rows(int people, boolean equal, int numOfRows, int differentTilesPerRow) throws InvalidNumberOfPlayersException {
        super(people, equal ? 7 : 6);
        this.equal=equal;
        this.numOfRows=numOfRows;
        this.differentTilesPerRow=differentTilesPerRow;
    }

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        HashSet<TileColor> foundColors;
        int count=0;

        for( int i = 0; i < r; i++ ){
            foundColors = new HashSet<TileColor>();
            for( int j = 0; j < c; j++ ){
                if( s.getTile(new Coordinates(i,j)) != null ) foundColors.add(s.getTile(new Coordinates(i,j)).getColor());
            }
            if( equal && foundColors.size() <= differentTilesPerRow ){
                if( ++count == numOfRows ) return true;
            }
            if( !equal && foundColors.size() >= differentTilesPerRow ){
                if( ++count == numOfRows ) return true;
            }
        }
        return false;
    }

}
