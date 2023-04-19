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

        int c = Shelf.getColumns();
        int r = Shelf.getRows();
        boolean found;
        TileColor col;
        int x, y;

        for( int i=0; i<r; i++ ){
            for( int j=0; j<c; j++ ){
                for( List<Coordinates> shape : shapes ){
                    found = true;
                    // if the first of the shape's tiles is inside the shelf (it does not need to be the first one)
                    if( ( i+shape.get(0).getX() < r ) && ( j+shape.get(0).getY() < c ) ){
                        // get its color
                        col = s.getTile(new Coordinates(i+shape.get(0).getX(),j+shape.get(0).getY())).getColor();
                        // and check if every shape's tile is inside the shelf and has the same color
                        for( Coordinates coord : shape ) {
                            x = coord.getX();
                            y = coord.getY();
                            if ( ( i+x >= r ) || ( j+y >= c ) || !s.getTile(new Coordinates(i+x, j+y)).getColor().equals(col)){
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
