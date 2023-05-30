package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;

/**
 * The Stair class represents a global goal that requires having five columns with ascending or descending heights on the shelf.
 * Each column must have one more tile than the previous column.
 * The tiles can have different colors.
 */
public class Stair extends GlobalGoal {
    /**
     * Constructs a Stair instance with the specified number of players.
     *
     * @param people the number of players in the game
     * @throws InvalidNumberOfPlayersException if the number of players is invalid
     */
    public Stair(int people) throws InvalidNumberOfPlayersException {
        super(people, 12);
    }

    /**
     * Checks if the specified shelf satisfies the condition of having five columns with ascending or descending heights.
     * Each column must have one more tile than the previous column.
     * The tiles can have different colors.
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

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int stairLength = 5;
        boolean stair;

        //checking if there is an asc stair
        for( int i = stairLength-1; i < r; i++ ){
            for( int j = 0; j <= c-stairLength; j++ ){
                stair = true;
                for( int k=0; ( k < stairLength ) && stair; k++ ){
                    if( s.getTile(new Coordinates(i-k,j+k)) == null ) stair = false;
                    else if( ( i-k-1 >= 0 ) && ( s.getTile(new Coordinates(i-k-1,j+k)) != null ) ) stair = false;
                }
                if( stair ) return true;
            }
        }

        //checking if there is a desc stair
        for( int i = stairLength-1; i < r; i++ ){
            for( int j = stairLength-1; j < c; j++){
                stair = true;
                for( int k=0; ( k < stairLength ) && stair; k++ ){
                    if( s.getTile(new Coordinates(i-k,j-k)) == null ) stair = false;
                    else if( ( i-k-1 >= 0 ) && ( s.getTile(new Coordinates(i-k-1,j-k)) != null ) ) stair = false;
                }
                if( stair ) return true;
            }
        }

    return false;
    }

}
