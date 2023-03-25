package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Tile;

public class TwoTiles extends GlobalGoal {

    public TwoTiles(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        boolean[][] checked= new boolean[r][c];

        if( s == null ){
            throw new MissingShelfException();
        }

        for( int i=0; i<r; i++ ){
            for( int j=0; j<c; j++ ){
                checked[i][j] = false;
            }
        }
        int currentG = 0;


        for( int i=0; i<r && currentG<6; i++ ){
            for( int j=0; j<c && currentG<6; j++ ){
                if( checked[i][j] == false ){
                    if( checkFromThisTile(s,new Coordinates(i,j),checked) >=2 ) currentG++;
                }
            }
        }
        if( currentG == 6 ) return true;
        return false;
    }

    private int checkFromThisTile(Shelf s,Coordinates coord, boolean[][] checked){
        Tile t = s.getTile(coord);
        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int i = coord.getX();
        int j = coord.getY();
        checked[i][j] = true;
        int res = 1;
        Tile temp;

        if( t == null ) return 0;

        //checking the Tile above this one
        if( ( i>0 ) && ( checked[i-1][j] == false ) ){
            temp = s.getTile(new Coordinates(i-1,j) );
            if( ( temp != null ) && temp.getColor().equals(t.getColor()) ) res += checkFromThisTile(s,new Coordinates(i-1,j),checked);
        }

        //checking the Tile under this one
        if( ( i < r-1 ) && ( checked[i+1][j] == false ) ){
            temp = s.getTile(new Coordinates(i+1,j));
            if( ( temp!=null ) && temp.getColor().equals(t.getColor()) ) res += checkFromThisTile(s,new Coordinates(i+1,j),checked);
        }

        //checking the Tile to the left of this one
        if( ( j>0 ) && ( checked[i][j-1] == false ) ){
            temp = s.getTile(new Coordinates(i,j-1));
            if( ( temp != null ) && temp.getColor().equals(t.getColor()) ) res += checkFromThisTile(s,new Coordinates(i,j-1),checked);
        }

        //checking the Tile to the right of this one
        if( ( j < c-1 ) && ( checked[i][j+1] == false ) ){
            temp = s.getTile(new Coordinates(i,j+1));
            if( ( temp != null ) && temp.getColor().equals(t.getColor()) ) res += checkFromThisTile(s,new Coordinates(i,j+1),checked);
        }

        return res;
    }
}
