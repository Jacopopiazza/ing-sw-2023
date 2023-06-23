package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;

/**
 * The Square class represents a global goal that requires having two separated groups of 2x2 tiles of the same color on the shelf.
 */
public class Square extends GlobalGoal {
    /**
     * Constructs a Square instance with the specified number of players.
     *
     * @param people the number of players in the game
     * @throws InvalidNumberOfPlayersException if the number of players is invalid
     */
    public Square(int people) throws InvalidNumberOfPlayersException {
        super(people, 1);
        this.description = "Two groups each containing 4 tiles of " +
                "the same type in a 2x2 square. The tiles " +
                "of one square can be different from " +
                "those of the other square.";
    }

    /**
     * Checks if the specified shelf satisfies the condition of having two separated groups of 2x2 tiles of the same color.
     *
     * @param s the shelf to check
     * @return true if the shelf satisfies the condition, false otherwise
     * @throws MissingShelfException if the shelf is null
     */
    @Override
    public boolean check(Shelf s) throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int c = Shelf.getColumns();
        int r = Shelf.getRows();
        int squareSide = 2;
        Coordinates firstSquare = null;

        for( int i = 0; i <= r-squareSide; i++ ){
            for( int j = 0; j <= c-squareSide; j++ ){
                Coordinates coord = new Coordinates(i, j);
                Tile temp = s.getTile(coord);
                boolean isASquare = true;
                //here I will check if there is a Tile at [i][j] and, if this is the case,
                //I will check whether that Tile is not contained in the first found square (if it was already found)
                if( temp == null ) isASquare = false;
                if( isASquare && ( firstSquare != null ) ){
                    for( int k = 0; ( k < squareSide ) && isASquare; k++ ){
                        for( int h = 0; ( h < squareSide ) && isASquare; h++ ){
                            if( coord.equals(new Coordinates(firstSquare.getROW()+k, firstSquare.getCOL()+h)) ) isASquare = false;
                        }
                    }
                }

                // here I will check if there is a square around this Tile
                if( isASquare ){
                    for( int k = 0; ( k < squareSide ) && isASquare; k++ ){
                        for( int h = 0; ( h < squareSide ) && isASquare; h++ ){
                            if( ( s.getTile(new Coordinates(i+k, j+h)) == null ) || !temp.getColor().equals(s.getTile(new Coordinates(i+k, j+h)).getColor()) ) isASquare = false;
                        }
                    }
                }

                // here I will check whether the Tiles around the square have a different color
                if( isASquare ){
                    for( int k = 0; ( k < squareSide ) && isASquare; k++ ){
                        if( ( i-1 >= 0 ) && ( s.getTile(new Coordinates(i-1, j+k)) != null ) && temp.getColor().equals(s.getTile(new Coordinates(i-1, j+k)).getColor()) ) isASquare = false;
                        if( ( j-1 >= 0 ) && ( s.getTile(new Coordinates(i+k, j-1)) != null ) && temp.getColor().equals(s.getTile(new Coordinates(i+k, j-1)).getColor()) ) isASquare = false;
                        if( ( i+squareSide < r ) && ( s.getTile(new Coordinates(i+squareSide, j+k)) != null ) && temp.getColor().equals(s.getTile(new Coordinates(i+squareSide, j+k)).getColor()) ) isASquare = false;
                        if( ( j+squareSide < c ) && ( s.getTile(new Coordinates(i+k, j+squareSide)) != null ) && temp.getColor().equals(s.getTile(new Coordinates(i+k, j+squareSide)).getColor()) ) isASquare = false;
                    }
                }

                if( isASquare ){
                    if( firstSquare == null ) firstSquare = coord;
                    else return true;
                }
            }
        }
        return false;
    }

}
