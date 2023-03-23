package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;


public class Diagonal extends GlobalGoal {

    public Diagonal(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        if( s == null ){
            throw new MissingShelfException();
        }

        for(int i=0;i<r-4;i++){
            for(int j=0;j<c-4;j++){
                if(s.getTile(new Coordinates(i,j))!=null){
                    boolean diagonalFound = true;
                    for(int k=0;k<4 && diagonalFound;k++){
                        if(!s.getTile(new Coordinates(i+k,j+k)).getColor().equals(s.getTile(new Coordinates(i+k+1,j+k+1)).getColor())) diagonalFound=false;
                    }
                    if(diagonalFound) return true;
                }
            }
        }

        for(int i=4;i<r;i++){
            for(int j=0;j<c-4;j++){
                if(s.getTile(new Coordinates(i,j))!=null){
                    boolean diagonalFound = true;
                    for(int k=0;k<4 && diagonalFound;k++){
                        if(!s.getTile(new Coordinates(i-k,j+k)).getColor().equals(s.getTile(new Coordinates(i-k-1,j+k+1)).getColor())) diagonalFound=false;
                    }
                    if(diagonalFound) return true;
                }
            }
        }
        return false;

    }
}
























