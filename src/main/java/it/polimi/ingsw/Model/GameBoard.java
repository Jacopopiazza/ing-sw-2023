package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Utilities.Config;
import it.polimi.ingsw.ModelView.GameBoardView;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The GameBoard class represents the game board in the game.
 */
public class GameBoard {
    private final Map<Coordinates, Tile> board;  // The tiles on the game board

    /**
     * Constructs a new {@code GameBoard} object for the specified number of players.
     *
     * @param numberOfPlayers the number of players in the game
     * @throws InvalidNumberOfPlayersException if the number of players is invalid
     */
    public GameBoard(int numberOfPlayers) throws InvalidNumberOfPlayersException{
        if( numberOfPlayers > Config.getInstance().getMaxNumberOfPlayers() ) {
            throw new InvalidNumberOfPlayersException();
        }

        board = new HashMap<>();
        List<Coordinates> a = Config.getInstance().getGameBoardCoordinates(numberOfPlayers);
        for( Coordinates coords : a )
            board.put(coords.clone(), null);
    }

    /**
     * Gets the ModelView representation of the game board.
     *
     * @return the {@link GameBoardView} object representing the game board
     */
    public GameBoardView getView() {
        return new GameBoardView(this);
    }

    /**
     * Gets the set of coordinates on the game board.
     *
     * @return the set of {@link Coordinates} representing the coordinates on the game board
     */
    public Set<Coordinates> getCoords() {
        return Collections.unmodifiableSet(board.keySet());
    }

    /**
     * Gets the tile at the specified coordinates on the game board.
     *
     * @param coordinates the {@link Coordinates} of the tile
     * @return the {@link Tile} at the specified coordinates, or null if there is no tile
     * @throws InvalidCoordinatesForCurrentGameException if the coordinates are invalid
     */
    public Tile getTile(Coordinates coordinates) throws InvalidCoordinatesForCurrentGameException{
        if( !board.containsKey(coordinates) ) {
            throw new InvalidCoordinatesForCurrentGameException();
        }
        return board.get(coordinates) != null ? board.get(coordinates).clone() : null;
    }

    /**
     * Sets the {@code Tile} at the specified {@code Coordinates} on the game board.
     *
     * @param coordinates the {@link Coordinates} of the tile
     * @param tile        the {@link Tile} to set
     * @throws InvalidCoordinatesForCurrentGameException if the coordinates are invalid
     */
    public void setTile(Coordinates coordinates, Tile tile) throws InvalidCoordinatesForCurrentGameException{
        if( !board.containsKey(coordinates) ) {
            throw new InvalidCoordinatesForCurrentGameException();
        }
        board.put(coordinates, ( tile == null ) ? null : tile.clone() );
    }

    /**
     * Checks if the game board needs to be refilled with tiles.
     *
     * @return true if the game board needs to be refilled, false otherwise
     */
    public boolean toRefill() {
        Coordinates up, down, right, left;
        boolean notYet;
        for( Coordinates c : board.keySet() ) {
            try {
                if(isPickable(c)) {
                    //noinspection DuplicatedCode
                    up = new Coordinates(c.getROW(), c.getCOL() - 1);
                    down = new Coordinates(c.getROW(), c.getCOL() + 1);
                    right = new Coordinates(c.getROW() + 1, c.getCOL());
                    left = new Coordinates(c.getROW() - 1, c.getCOL());
                    notYet = false;
                    notYet = notYet || (board.containsKey(up) && isPickable(up));
                    notYet = notYet || (board.containsKey(down) && isPickable(down));
                    notYet = notYet || (board.containsKey(right) && isPickable(right));
                    notYet = notYet || (board.containsKey(left) && isPickable(left));
                    if(notYet) return false;
                }
            } catch (InvalidCoordinatesForCurrentGameException ex) {
                System.out.println("Something went wrong");
                return false;
            }
        }
        return true;
    }

    /**
     * Checks the score for the board goals based on the tiles in the player's shelf.
     *
     * @param shelf the player's {@link Shelf}
     * @return the total score based on the board goals
     * @throws MissingShelfException       if the shelf is missing
     * @throws ColumnOutOfBoundsException if the column index is out of bounds
     */
    public static int checkBoardGoal(Shelf shelf) throws MissingShelfException, ColumnOutOfBoundsException {
        if( shelf == null ) {
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int totalScore, currentGroup;
        int indexOfLastCheck;

        boolean[][] checked = new boolean[r][c];

        for( int i = 0; i < r; i++ ) {
            for( int j = 0; j < c; j++ ) {
                checked[i][j] = false;
            }
        }

        totalScore = 0;
        for( int i = 0; i < r; i++ ) {
            for( int j = 0; j < c; j++ ) {
                if(!checked[i][j]) {
                    currentGroup = checkFromThisTile(shelf, new Coordinates(i,j), checked);
                    for( Config.BoardGoalScore t : Config.getInstance().getSortedBoardGoals() ) {
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

    /**
     * Recursively checks the number of {@code Tile}s connected to the given tile on the player's {@code Shelf}.
     *
     * @param shelf   the player's {@link Shelf}
     * @param coord   the {@link Coordinates} of the tile to check
     * @param checked a 2D boolean array to keep track of checked tiles
     * @return the number of connected tiles
     */
    private static int checkFromThisTile(Shelf shelf, Coordinates coord, boolean[][] checked) {
        Tile t = shelf.getTile(coord);
        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int i = coord.getROW();
        int j = coord.getCOL();
        checked[i][j] = true;
        int res = 1;
        Tile temp;

        if( t == null ) return 0;

        //checking the Tile above this one
        if( i>0 && (!checked[i - 1][j]) ) {
            temp = shelf.getTile(new Coordinates(i-1,j));
            if( temp!=null && temp.getColor().equals(t.getColor()) ) res+=checkFromThisTile(shelf,new Coordinates(i-1,j),checked);
        }

        //checking the Tile under this one
        if( ( i < r-1 ) && (!checked[i + 1][j]) ) {
            temp = shelf.getTile(new Coordinates(i+1,j));
            if( temp!=null && temp.getColor().equals(t.getColor()) ) res+=checkFromThisTile(shelf,new Coordinates(i+1,j),checked);
        }

        //checking the Tile to the left of this one
        if( j>0 && !checked[i][j - 1]) {
            temp = shelf.getTile(new Coordinates(i,j-1));
            if( temp != null && temp.getColor().equals(t.getColor()) ) res+=checkFromThisTile(shelf,new Coordinates(i,j-1),checked);
        }

        //checking the Tile to the right of this one
        if( ( j < c-1 ) && (!checked[i][j + 1]) ) {
            temp = shelf.getTile(new Coordinates(i,j+1));
            if( temp != null && temp.getColor().equals(t.getColor()) ) res+=checkFromThisTile(shelf,new Coordinates(i,j+1),checked);
        }

        return res;
    }

    /**
     * Checks if a tile at the given {@code Coordinates} is pickable.
     *
     * @param coordinates the {@link Coordinates} of the tile
     * @return true if the tile is pickable, false otherwise
     * @throws InvalidCoordinatesForCurrentGameException if the coordinates are invalid
     */
    public boolean isPickable(Coordinates coordinates) throws InvalidCoordinatesForCurrentGameException {
        if( !board.containsKey(coordinates) ) {
            throw new InvalidCoordinatesForCurrentGameException();
        }

        Coordinates up, down, right, left;
        //noinspection DuplicatedCode
        up = new Coordinates(coordinates.getROW(), coordinates.getCOL() - 1);
        down = new Coordinates(coordinates.getROW(), coordinates.getCOL() + 1);
        right = new Coordinates(coordinates.getROW() + 1, coordinates.getCOL());
        left = new Coordinates(coordinates.getROW() - 1, coordinates.getCOL());

        if( board.get(coordinates) == null ) return false;

        if( !board.containsKey(up) || ( board.get(up) == null ) ) return true;
        if( !board.containsKey(down) || ( board.get(down) == null ) ) return true;
        if( !board.containsKey(right) || ( board.get(right) == null ) ) return true;
        return !board.containsKey(left) || (board.get(left) == null);
    }

    /**
     * Checks if the chosen tiles meet the required conditions.
     *
     * @param chosenTiles the {@link Coordinates} of the chosen tiles
     * @return true if the chosen tiles are valid, false otherwise
     */
    public boolean checkChosenTiles(Coordinates[] chosenTiles) {
        //checking that the length of the array is at most 3
        if(chosenTiles.length > 3) return false;

        //checking there are no duplicates and that they are all pickable
        for(Coordinates c : chosenTiles) {
            if(c == null) return false;
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
            if(row && Arrays.stream(chosenTiles).filter(x -> c.getCOL()-1 == x.getCOL() ||
                    c.getCOL()+1 == x.getCOL()).collect(Collectors.toList()).size() == 0) return false;

            if(column && Arrays.stream(chosenTiles).filter(x -> c.getROW()-1 == x.getROW() ||
                    c.getROW()+1 == x.getROW()).collect(Collectors.toList()).size() == 0) return false;
        }
        return true;
    }

}
