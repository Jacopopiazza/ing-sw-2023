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

    public GameBoardView(GameBoard gameBoard){
        this.board = new HashMap<>();
        pickables = new HashSet<>();
        for(Coordinates c : gameBoard.getCoords()){
            this.board.put(c,( gameBoard.getTile(c) == null ) ? null : new TileView(gameBoard.getTile(c)));
            if(gameBoard.getTile(c) != null && gameBoard.isPickable(c)) pickables.add(c);
        }
    }
    public Set<Coordinates> getCoords(){
        return Collections.unmodifiableSet(board.keySet());
    }

    public TileView getTile(Coordinates c) throws InvalidCoordinatesForCurrentGameException {
        if( !board.containsKey(c) ){
            throw new InvalidCoordinatesForCurrentGameException();
        }
        return board.get(c);
    }

    public boolean isPickable(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
        if( !board.containsKey(c) ){
            throw new InvalidCoordinatesForCurrentGameException();
        }
        return pickables.contains(c);
    }

}
