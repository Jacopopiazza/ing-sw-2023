package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;


public class Stair extends GlobalGoal {
    public Stair(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    // Five columns with asc/desc height:
    // starting from the first column to the left or right
    // every next column must have one more tile.
    // The tiles can have different colors.
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
