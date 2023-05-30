package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Exceptions.InvalidCoordinatesForCurrentGameException;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GameBoard;

import java.io.Serializable;
import java.util.*;

public class GameBoardView implements Serializable {
    private static final long serialVersionUID=1L;
    private final Map<Coordinates, TileView> board;
    private final Set<Coordinates> pickables;

    /**
     * Constructs a GameBoardView object based on the provided GameBoard.
     *
     * @param gameBoard the game board from which to create the view
     */
    public GameBoardView(GameBoard gameBoard){
        this.board = new HashMap<>();
        pickables = new HashSet<>();
        for(Coordinates c : gameBoard.getCoords()){
            this.board.put(c,( gameBoard.getTile(c) == null ) ? null : new TileView(gameBoard.getTile(c)));
            if(gameBoard.getTile(c) != null && gameBoard.isPickable(c)) pickables.add(c);
        }
    }

    /**
     * Returns the set of coordinates on the game board.
     *
     * @return the set of coordinates
     */
    public Set<Coordinates> getCoords(){
        return Collections.unmodifiableSet(board.keySet());
    }

    /**
     * Retrieves the tile view at the specified coordinates.
     *
     * @param c the coordinates of the tile
     * @return the tile view at the specified coordinates
     * @throws InvalidCoordinatesForCurrentGameException if the coordinates are invalid for the current game
     */
    public TileView getTile(Coordinates c) throws InvalidCoordinatesForCurrentGameException {
        if( !board.containsKey(c) ){
            throw new InvalidCoordinatesForCurrentGameException();
        }
        return board.get(c);
    }

    /**
     * Checks if the tile at the specified coordinates is pickable.
     *
     * @param c the coordinates of the tile
     * @return true if the tile is pickable, false otherwise
     * @throws InvalidCoordinatesForCurrentGameException if the coordinates are invalid for the current game
     */
    public boolean isPickable(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
        if( !board.containsKey(c) ){
            throw new InvalidCoordinatesForCurrentGameException();
        }
        return pickables.contains(c);
    }

}
