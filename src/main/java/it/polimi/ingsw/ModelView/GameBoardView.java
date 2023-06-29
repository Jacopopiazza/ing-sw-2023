package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Exceptions.InvalidCoordinatesForCurrentGameException;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GameBoard;
import it.polimi.ingsw.Model.Player;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * The {@code GameBoardView} class represents the immutable version of the {@link it.polimi.ingsw.Model.GameBoard}.
 * It provides a snapshot of the game board's attributes in a serializable format.
 */
public class GameBoardView implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;
    private final Map<Coordinates, TileView> board;
    private final Set<Coordinates> pickables;

    /**
     * Constructs a new {@code GameBoardView} object based on the provided GameBoard.
     *
     * @param gameBoard the {@link it.polimi.ingsw.Model.GameBoard} from which it creates the ModelView
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
     * @return the set of {@link Coordinates}
     */
    public Set<Coordinates> getCoords(){
        return Collections.unmodifiableSet(board.keySet());
    }

    /**
     * Retrieves the {@code TileView} at the specified coordinates.
     *
     * @param c the {@link Coordinates} of the tile
     * @return the {@link TileView} at the specified {@link Coordinates}
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
     * @return {@code true} if the tile is pickable, {@code false} otherwise
     * @throws InvalidCoordinatesForCurrentGameException if the coordinates are invalid for the current game
     */
    public boolean isPickable(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
        if( !board.containsKey(c) ){
            throw new InvalidCoordinatesForCurrentGameException();
        }
        return pickables.contains(c);
    }

}
