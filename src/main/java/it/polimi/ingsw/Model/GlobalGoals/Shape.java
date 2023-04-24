package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.Exceptions.*;

import java.util.ArrayList;
import java.util.List;

public class Shape extends GlobalGoal {

    List<List<Coordinates>> shapes;

    public Shape(int people, List<List<Coordinates>> s) throws InvalidNumberOfPlayersException {
        super(people);
        shapes= new ArrayList<List<Coordinates>>();
        List<Coordinates> shape;
        for ( List<Coordinates> l: s ) {
            shape = new ArrayList<Coordinates>();
            for (Coordinates c: l){
                shape.add(c);
            }
            shapes.add(shape);
        }
    }

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int columns = Shelf.getColumns();
        int rows = Shelf.getRows();
        boolean found;
        TileColor color;
        int r, c;

        for( int i=0; i<rows; i++ ){
            for( int j=0; j<columns; j++ ){
                for( List<Coordinates> shape : shapes ){
                    found = true;
                    // if the first of the shape's tiles is inside the shelf (it does not need to be the first one)
                    if( ( i+shape.get(0).getROW() < rows ) && ( j+shape.get(0).getCOL() < columns ) ){
                        // get its color
                        color = s.getTile(new Coordinates(i+shape.get(0).getROW(),j+shape.get(0).getCOL())).getColor();
                        // and check if every shape's tile is inside the shelf and has the same color
                        for( Coordinates coord : shape ) {
                            r = coord.getROW();
                            c = coord.getCOL();
                            if ( ( i+r >= rows ) || ( j+c >= columns ) || !s.getTile(new Coordinates(i+r, j+c)).getColor().equals(color)){
                                found = false;
                                break;
                            }
                        }
                    }
                    else found = false;

                    if( found ) return true;
                }
            }
        }
        return false;
    }

}
