package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.TileColor;

public class XShape extends GlobalGoal {

    // Five tiles of the same color that create the shape of an 'X'
    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if(s==null) throw new MissingShelfException();

        for(int row = 0; row < s.getRows() - 2; row++){
            for(int column = 0; column < s.getColumns() - 2; column++){
                // check if there is an 'X' shape of tiles
                if( s.getTile(new Coordinates(row, column)) != null
                        && s.getTile(new Coordinates(row, column + 2)) != null
                        && s.getTile(new Coordinates(row + 2, column)) != null
                        && s.getTile(new Coordinates(row + 2, column + 2)) != null
                        && s.getTile(new Coordinates(row + 1, column + 1)) != null ){
                    // then check if the 'X' shape Tiles have the same color
                    TileColor color = s.getTile(new Coordinates(row, column)).getColor();
                    if( s.getTile(new Coordinates(row, column + 2)).getColor().equals(color)
                            && s.getTile(new Coordinates(row + 2, column)).getColor().equals(color)
                            && s.getTile(new Coordinates(row + 2, column)).getColor().equals(color)
                            && s.getTile(new Coordinates(row + 2, column + 2)).getColor().equals(color)
                            && s.getTile(new Coordinates(row + 1, column + 1)).getColor().equals(color) ){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
