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
        int d = 5; //diagonal length
        boolean diagonalFound;

        if( s == null ){
            throw new MissingShelfException();
        }

        //diagonal
        for( int i=0; i<=r-d; i++ ){
            for( int j=0; j<=c-d; j++ ){
                if( s.getTile(new Coordinates(i,j)) != null ){
                    diagonalFound = true;
                    for( int k=0; ( k<d-1 ) && diagonalFound; k++ ){
                        if( !s.getTile( new Coordinates(i+k,j+k)).getColor().equals(s.getTile(new Coordinates(i+k+1,j+k+1)).getColor()) ) diagonalFound = false;
                    }
                    if( diagonalFound ) return true;
                }
            }
        }

        //anti-diagonal
        for( int i=d-1; i<r ; i++ ){
            for( int j=0; j<=c-d; j++ ){
                if( s.getTile(new Coordinates(i,j)) != null ){
                    diagonalFound = true;
                    for( int k=0; ( k<d-1 ) && diagonalFound; k++ ){
                        if( !s.getTile(new Coordinates(i-k,j+k)).getColor().equals(s.getTile(new Coordinates(i-k-1,j+k+1)).getColor()) ) diagonalFound = false;
                    }
                    if( diagonalFound ) return true;
                }
            }
        }
        return false;

    }
}
























