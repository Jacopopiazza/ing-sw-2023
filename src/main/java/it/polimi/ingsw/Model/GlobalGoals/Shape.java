package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.Tile;
import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;

import java.util.ArrayList;
import java.util.List;

/**
 * The Shape class represents a global goal that requires matching a specific shape of tiles on the shelf.
 * It extends the {@code GlobalGaol} abstract class.
 */
public class Shape extends GlobalGoal {

    List<List<Coordinates>> shapes;

    /**
     * Constructs a new {@code Shape} global goal with the specified number of players and shape.
     *
     * @param people the number of players in the game
     * @param s      the shape to match
     * @throws InvalidNumberOfPlayersException if the number of players is invalid
     */
    public Shape(int people, List<List<Coordinates>> s) throws InvalidNumberOfPlayersException {
        super(people, myId(s));
        shapes = new ArrayList<List<Coordinates>>();
        List<Coordinates> shape;
        for( List<Coordinates> l : s ){
            shape = new ArrayList<Coordinates>();
            for( Coordinates c : l )
                shape.add(c);
            shapes.add(shape);
        }
        if( myId(s) == 10 )
            this.description = "Five tiles of the same type forming an X.";
        if( myId(s) == 11 )
            this.description = "Five tiles of the same type forming a diagonal.";
    }

    /**
     * Checks if the specified shelf satisfies the condition of having a matching shape of tiles.
     *
     * @param s the shelf to check
     * @return true if the shelf satisfies the condition, false otherwise
     * @throws MissingShelfException if the shelf is null
     */
    @Override
    public boolean check(Shelf s) throws MissingShelfException {
        if( s == null ) {
            throw new MissingShelfException();
        }

        int columns = Shelf.getColumns();
        int rows = Shelf.getRows();
        boolean found;
        TileColor color;
        int r, c;

        for( int i = 0; i < rows; i++ ){
            for( int j = 0; j < columns; j++ ){
                for( List<Coordinates> shape : shapes ){
                    found = true;
                    // if the first tile coordinates are inside the bounds of the shelf (it does not need to be the first one)
                    if( ( ( i + shape.get(0).getROW() ) < rows ) && ( ( j + shape.get(0).getCOL() ) < columns) ){
                        //if at that coordinates there is effectively a tile
                        Tile tile = s.getTile(new Coordinates( i + shape.get(0).getROW(), j + shape.get(0).getCOL()) );
                        if( tile != null ){
                            // get its color
                            color = tile.getColor();
                            // and check if every shape's tile is inside the shelf and has the same color
                            for( Coordinates coord : shape ){
                                r = coord.getROW();
                                c = coord.getCOL();
                                if( ( ( i + r ) >= rows ) || ( ( j + c ) >= columns ) ||
                                        s.getTile(new Coordinates(i+r, j+c)) == null || !s.getTile(new Coordinates(i+r, j+c)).getColor().equals(color) ){
                                    found = false;
                                    break;
                                }
                            }
                        }
                        else found = false;
                    }
                    else found = false;

                    if( found ) return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines the ID of the shape based on the given shape configuration.
     *
     * @param shape the shape configuration
     * @return the ID of the shape, or -1 if it doesn't match any predefined shapes
     */
    private static int myId(List<List<Coordinates>> shape) {
        // To add a new Shape Global Goal, add its name to the names[] and "Check if it's ..."
        boolean mayBe;
        boolean tempFlag;

        // Check if it's Diagonal
        mayBe = true;
        for( List<Coordinates> l1 : shape ) {
            tempFlag = false;
            for( List<Coordinates> l2 : Config.getInstance().getDiagonalsFromJSON() ) {
                if( !tempFlag && l1.containsAll(l2) && (l1.size() == l2.size()) ) tempFlag = true;
            }
            if( !tempFlag ) mayBe = false;
        }
        if( mayBe ) return 11;

        // Check if it's XShape
        mayBe = true;
        for( List<Coordinates> l1 : shape ){
            tempFlag = false;
            for( List<Coordinates> l2 : Config.getInstance().getXShapeFromJSON() ){
                if( !tempFlag && l1.containsAll(l2) && ( l1.size() == l2.size() ) ) tempFlag = true;
            }
            if( !tempFlag ) mayBe = false;
        }
        if( mayBe ) return 10;

        return -1;
    }
}