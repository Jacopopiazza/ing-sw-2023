package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Exceptions.InvalidCoordinatesForCurrentGameException;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.GameBoard;
import it.polimi.ingsw.Model.Tile;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameBoardView implements Serializable {

    private static final long serialVersionUID=1L;
    private final Map<Coordinates, TileView> board;

    public GameBoardView(GameBoard gameBoard){
        this.board = new HashMap<>();
        for(Coordinates c : gameBoard.getCoords()){
            this.board.put(c,new TileView(gameBoard.getTile(c)));
        }
    }
    public Set<Coordinates> getCoords(){
        return Collections.unmodifiableSet(board.keySet());
    }

    public TileView getTile(Coordinates c) throws InvalidCoordinatesForCurrentGameException {
        if( !board.containsKey(c) ){
            throw new InvalidCoordinatesForCurrentGameException();
        }
        return board.get(c) != null ? board.get(c) : null;
    }

    public boolean isPickable(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
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

}
