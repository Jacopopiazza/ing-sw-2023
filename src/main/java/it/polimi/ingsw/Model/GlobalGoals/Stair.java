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
        if(s==null) throw new MissingShelfException();

        int tileCounter;
        boolean stair = true;

        // asc. order: starts from the first column
        for(int column = 0; column < s.getColumns(); column++){
            tileCounter = 0;
            for(int row = s.getRows() - 1; row >= 0; row--){
                if(s.getTile(new Coordinates(row, column)) != null){
                    tileCounter++;
                }else{
                    //interrupt the inner cycle if there are no more tiles in that column
                    break;
                }
            }
            // if there is not an asc. stair, check for the desc. stair
            if(tileCounter != (column + 1)){
                stair = false;
                break;
            }
        }

        // if there is an asc. stair, return true
        if(stair)
            return true;
        stair = true;

        //desc order: starts from the last column
        for(int column = s.getColumns() - 1; column >= 0; column--){
            tileCounter = 0;
            for(int row = s.getRows() - 1; row >= 0; row--){
                if(s.getTile(new Coordinates(row, column)) != null){
                    tileCounter++;
                }else{
                    //interrupt the inner cycle if there are no more tiles in that column
                    break;
                }
            }
            // if there is not an asc. stair, check for the desc. stair
            if(tileCounter != (column + 1)){
                stair = false;
                break;
            }
        }

        return stair;
    }
}
