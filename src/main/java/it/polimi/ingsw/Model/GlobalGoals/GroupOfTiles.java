package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Tile;
/**
 * The GroupOfTiles class represents a global goal that requires creating a certain number of groups of tiles on the shelf.
 * The groups are formed by tiles of the same color adjacent to each other.
 */
public class GroupOfTiles extends GlobalGoal {
    private final int groupDim;
    private final int numOfGroups;

    /**
     * Returns the ID of the global goal based on the group dimension.
     *
     * @param g the group dimension
     * @return the ID of the global goal
     */
    private static int myId(int g){
        if( g == 2 )
            return 4;
        if( g == 4 )
            return 3;
        return -1;
    }

    /**
     * Constructs a GroupOfTiles instance with the specified number of players, group dimension, and number of groups.
     *
     * @param people           the number of players in the game
     * @param groupDim         the required size of each group of tiles
     * @param numOfGroups      the number of groups of tiles required to achieve the goal
     * @throws InvalidNumberOfPlayersException if the number of players is invalid
     */
    public GroupOfTiles(int people, int groupDim, int numOfGroups) throws InvalidNumberOfPlayersException {
        super(people, myId(groupDim));
        this.groupDim = groupDim;
        this.numOfGroups = numOfGroups;
        if( myId(groupDim) == 4 ) this.description = "Six groups each containing at least " +
                "2 tiles of the same type (not necessarily " +
                "in the depicted shape). " +
                "The tiles of one group can be different " +
                "from those of another group.";
        if( myId(groupDim) == 3 ) this.description = "Four groups each containing at least " +
                "4 tiles of the same type (not necessarily " +
                "in the depicted shape). " +
                "The tiles of one group can be different " +
                "from those of another group.";
    }

    /**
     * Checks if the specified shelf satisfies the condition of having the required number of groups of tiles.
     *
     * @param s the shelf to check
     * @return true if the shelf satisfies the condition, false otherwise
     * @throws MissingShelfException   if the shelf is null
     */
    @Override
    public boolean check(Shelf s) throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        boolean[][] checked = new boolean[r][c];
        int currentG = 0;

        for( int i=0; i<r; i++ ){
            for( int j=0; j<c ; j++ ){
                if( !checked[i][j] ){
                    if( checkFromThisTile(s,new Coordinates(i,j),checked) >= groupDim ){
                        if( ++currentG == numOfGroups ) return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Recursively checks for groups of tiles starting from the specified coordinates on the shelf.
     *
     * @param s       the shelf to check
     * @param coords  the coordinates to start checking from
     * @param checked a 2D array to keep track of checked tiles
     * @return the size of the group of tiles starting from the specified coordinates
     */
    private int checkFromThisTile(Shelf s, Coordinates coords, boolean[][] checked){
        Tile t = s.getTile(coords);
        if( t == null ) return 0;

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int i = coords.getROW();
        int j = coords.getCOL();
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
