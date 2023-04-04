package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Tile;

public class FourTiles extends GlobalGoal {
    public FourTiles(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    @Override
    public boolean check(Shelf s) throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int GroupDim = 4;
        int numOfGroups = 4;
        boolean[][] checked = new boolean[r][c];
        int currentG = 0;

        for( int i=0; i<r; i++ ){
            for( int j=0; j<c ; j++ ){
                if( !checked[i][j] ){
                    if( checkFromThisTile(s,new Coordinates(i,j),checked) >= GroupDim ){
                        if( ++currentG == numOfGroups ) return true;
                    }
                }
            }
        }
        return false;
    }

    private int checkFromThisTile(Shelf s,Coordinates coord, boolean[][] checked){
        Tile t = s.getTile(coord);
        if( t == null ) return 0;

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int i = coord.getX();
        int j = coord.getY();
        int res=1;
        Tile temp;

        checked[i][j] = true;

        //checking the Tile above this one
        if( ( i > 0 ) && !checked[i-1][j] ){
            temp = s.getTile(new Coordinates(i-1,j));
            if( ( temp != null ) && temp.getColor().equals(t.getColor()) ) res += checkFromThisTile(s,new Coordinates(i-1,j),checked);
        }

        //checking the Tile under this one
        if( ( i < r - 1 ) && !checked[i+1][j] ){
            temp = s.getTile(new Coordinates(i+1,j));
            if( ( temp != null ) && temp.getColor().equals(t.getColor()) ) res += checkFromThisTile(s,new Coordinates(i+1,j),checked);
        }

        //checking the Tile to the left of this one
        if( ( j > 0 ) && !checked[i][j-1] ){
            temp = s.getTile(new Coordinates(i,j-1));
            if( ( temp != null ) && temp.getColor().equals(t.getColor()) ) res += checkFromThisTile(s,new Coordinates(i,j-1),checked);
        }

        //checking the Tile to the right of this one
        if( ( j < c - 1 ) && !checked[i][j+1] ){
            temp = s.getTile(new Coordinates(i,j+1));
            if( ( temp != null ) && temp.getColor().equals(t.getColor()) ) res += checkFromThisTile(s,new Coordinates(i,j+1),checked);
        }

        return res;
    }

}
