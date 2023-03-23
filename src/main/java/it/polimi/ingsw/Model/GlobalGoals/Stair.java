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
        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        boolean stair;

        if( s == null ){
            throw new MissingShelfException();
        }

        //checking if there is an asc stair
        for(int i=4; i<r-1; i++){
            stair=true;
            for(int k=0;k<5 && stair;k++){
                if(s.getTile(new Coordinates(i-k,k)) == null) stair=false;
                else if(i-k-1>=0 && s.getTile(new Coordinates(i-k-1,k)) != null) stair=false;
            }
            if(stair) return true;
        }

        //checking if there is a desc stair
        for(int i=4; i<r-1; i++){
            stair=true;
            for(int k=0;k<5 && stair;k++){
                if(s.getTile(new Coordinates(i-k,c-1-k)) == null) stair=false;
                else if(i-k-1>=0 && s.getTile(new Coordinates(i-k-1,c-1-k)) != null) stair=false;
            }
            if(stair) return true;
        }
    return false;
    }
}
