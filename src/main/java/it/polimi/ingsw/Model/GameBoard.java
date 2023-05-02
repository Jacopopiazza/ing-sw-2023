package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;

import java.util.*;
import java.util.stream.Collectors;

public class GameBoard {
    private Map<Coordinates,Tile> board;

    public GameBoard(int people) throws InvalidNumberOfPlayersException{
        if( people > Config.getInstance().getMaxNumberOfPlayers() ){
            throw new InvalidNumberOfPlayersException();
        }

        board = new HashMap<>();
        List<Coordinates> a = Config.getInstance().getGameBoardCoordinates(people);
        for( Coordinates coords : a )
            board.put(coords.clone(), null);
    }

    public Set<Coordinates> getCoords(){
        return Collections.unmodifiableSet(board.keySet());
    }

    public Tile getTile(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
        if( !board.containsKey(c) ){
            throw new InvalidCoordinatesForCurrentGameException();
        }
        return board.get(c) != null ? board.get(c).clone() : null;
    }

    public void setTile(Coordinates c, Tile t) throws InvalidCoordinatesForCurrentGameException{
        if( !board.containsKey(c) ){
            throw new InvalidCoordinatesForCurrentGameException();
        }
        board.put(c, ( t == null ) ? null : t.clone() );
    }

    public boolean toRefill() {
        Coordinates up, down, right, left;
        boolean notYet;
        for( Coordinates c : board.keySet() ){
            try {
                if (isPickable(c)) {
                    up = new Coordinates(c.getROW(), c.getCOL() - 1);
                    down = new Coordinates(c.getROW(), c.getCOL() + 1);
                    right = new Coordinates(c.getROW() + 1, c.getCOL());
                    left = new Coordinates(c.getROW() - 1, c.getCOL());
                    notYet = false;
                    notYet = notYet || (board.containsKey(up) && isPickable(up));
                    notYet = notYet || (board.containsKey(down) && isPickable(down));
                    notYet = notYet || (board.containsKey(right) && isPickable(right));
                    notYet = notYet || (board.containsKey(left) && isPickable(left));
                    if (notYet) return false;
                }
            }
            //this exception should never be caught
            catch (InvalidCoordinatesForCurrentGameException ex){
                System.out.println("Something went wrong");
                return false;
            }
        }
        return true;
    }

    public static int checkBoardGoal(Shelf s) throws MissingShelfException{
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int totalScore, currentGroup;
        int indexOfLastCheck;

        boolean[][] checked = new boolean[r][c];

        for( int i = 0; i < r; i++ ){
            for( int j = 0; j < c; j++ ){
                checked[i][j] = false;
            }
        }

        totalScore = 0;
        for( int i = 0; i < r; i++ ){
            for( int j = 0; j < c; j++ ){
                if( checked[i][j] == false ){
                    currentGroup = checkFromThisTile(s, new Coordinates(i,j), checked);
                    for( Config.BoardGoalScore t : Config.getInstance().getSortedBoardGoals() ){
                        if( currentGroup == t.tiles() ) totalScore += t.score();
                    }
                    indexOfLastCheck = Config.getInstance().getSortedBoardGoals().length - 1;
                    if( currentGroup > Config.getInstance().getSortedBoardGoals()[indexOfLastCheck].tiles() )
                        totalScore += Config.getInstance().getSortedBoardGoals()[indexOfLastCheck].score();
                }
            }
        }

        return totalScore;
    }

    private static int checkFromThisTile(Shelf s, Coordinates coord, boolean[][] checked){
        Tile t = s.getTile(coord);
        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int i = coord.getROW();
        int j = coord.getCOL();
        checked[i][j] = true;
        int res = 1;
        Tile temp;

        if( t == null ) return 0;

        //checking the Tile above this one
        if( i>0 && ( checked[i-1][j] == false ) ){
            temp = s.getTile(new Coordinates(i-1,j));
            if( temp!=null && temp.getColor().equals(t.getColor()) ) res+=checkFromThisTile(s,new Coordinates(i-1,j),checked);
        }

        //checking the Tile under this one
        if( ( i < r-1 ) && ( checked[i+1][j] == false ) ){
            temp = s.getTile(new Coordinates(i+1,j));
            if( temp!=null && temp.getColor().equals(t.getColor()) ) res+=checkFromThisTile(s,new Coordinates(i+1,j),checked);
        }

        //checking the Tile to the left of this one
        if( j>0 && checked[i][j-1] == false ){
            temp = s.getTile(new Coordinates(i,j-1));
            if( temp != null && temp.getColor().equals(t.getColor()) ) res+=checkFromThisTile(s,new Coordinates(i,j-1),checked);
        }

        //checking the Tile to the right of this one
        if( ( j < c-1 ) && ( checked[i][j+1] == false ) ){
            temp = s.getTile(new Coordinates(i,j+1));
            if( temp != null && temp.getColor().equals(t.getColor()) ) res+=checkFromThisTile(s,new Coordinates(i,j+1),checked);
        }

        return res;
    }

    public boolean isPickable(Coordinates c) throws InvalidCoordinatesForCurrentGameException {
        if( !board.containsKey(c) ){
            throw new InvalidCoordinatesForCurrentGameException();
        }

        Coordinates up, down, right, left;
        up = new Coordinates(c.getROW(), c.getCOL() - 1);
        down = new Coordinates(c.getROW(), c.getCOL() + 1);
        right = new Coordinates(c.getROW() + 1, c.getCOL());
        left = new Coordinates(c.getROW() - 1, c.getCOL());

        if( board.get(c) == null ) return false;

        if( !board.containsKey(up) || ( board.get(up) == null ) ) return true;
        if( !board.containsKey(down) || ( board.get(down) == null ) ) return true;
        if( !board.containsKey(right) || ( board.get(right) == null ) ) return true;
        if( !board.containsKey(left) || ( board.get(left) == null ) ) return true;

        return false;
    }

    public boolean checkChosenTiles(Coordinates[] chosenTiles) {
        //checking that the length of the array is at most 3
        if(chosenTiles.length > 3) return false;

        //checking there are no duplicates and that they are all pickable
        for (Coordinates c : chosenTiles){
            try {
                if(Arrays.stream(chosenTiles).filter(x -> x.equals(c)).collect(Collectors.toList()).size() > 1
                        || !(isPickable(c)) ) {
                    return false;
                }
            } catch (InvalidCoordinatesForCurrentGameException e) {
                return false;
            }
        }

        //checking that the chosen tiles are on the same column or row on the board,
        //that they are one next ot the other
        boolean row = true;
        boolean column = true;
        for(int i=0;i<chosenTiles.length-1 && (row || column);i++) {
            if(chosenTiles[i].getROW() != chosenTiles[i+1].getROW()) row = false;
            if(chosenTiles[i].getCOL() != chosenTiles[i+1].getCOL()) column = false;

            if( !(row || column) ) return false;

            Coordinates c = chosenTiles[i];
            if(row && Arrays.stream(chosenTiles).filter(x -> c.getROW()-1 == x.getROW() ||
                    c.getROW()+1 == x.getROW()).collect(Collectors.toList()).size() == 0) return false;

            if(column && Arrays.stream(chosenTiles).filter(x -> c.getCOL()-1 == x.getCOL() ||
                    c.getCOL()+1 == x.getCOL()).collect(Collectors.toList()).size() == 0) return false;
        }
        return true;
    }

}
