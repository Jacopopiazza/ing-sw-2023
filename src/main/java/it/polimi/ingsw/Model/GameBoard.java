package it.polimi.ingsw.Model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameBoard {
    private Map<Coordinates,Tile> board;

    private GameBoard(){

    }

    public static GameBoard getGameBoard(int persone) throws InvalidNumberOfPlayersException{
        if(persone > Game.maxNumberOfPlayers){
            throw InvalidNumberOfPlayersException;
        }

        GameBoard gb = new GameBoard();
        gb.board = new HashMap<Coordinates,Tile>();


        gb.board.put(new Coordinates(1,3),null);
        gb.board.put(new Coordinates(1,4),null);

        gb.board.put(new Coordinates(2,3),null);
        gb.board.put(new Coordinates(2,4),null);
        gb.board.put(new Coordinates(2,5),null);

        gb.board.put(new Coordinates(3,2),null);
        gb.board.put(new Coordinates(3,3),null);
        gb.board.put(new Coordinates(3,4),null);
        gb.board.put(new Coordinates(3,5),null);
        gb.board.put(new Coordinates(3,6),null);
        gb.board.put(new Coordinates(3,7),null);

        gb.board.put(new Coordinates(4,1),null);
        gb.board.put(new Coordinates(4,2),null);
        gb.board.put(new Coordinates(4,3),null);
        gb.board.put(new Coordinates(4,4),null);
        gb.board.put(new Coordinates(4,5),null);
        gb.board.put(new Coordinates(4,6),null);
        gb.board.put(new Coordinates(4,7),null);

        gb.board.put(new Coordinates(5,1),null);
        gb.board.put(new Coordinates(5,2),null);
        gb.board.put(new Coordinates(5,3),null);
        gb.board.put(new Coordinates(5,4),null);
        gb.board.put(new Coordinates(5,5),null);
        gb.board.put(new Coordinates(5,6),null);

        gb.board.put(new Coordinates(6,3),null);
        gb.board.put(new Coordinates(6,4),null);
        gb.board.put(new Coordinates(6,5),null);

        gb.board.put(new Coordinates(7,4),null);
        gb.board.put(new Coordinates(7,5),null);

        if(persone > 2){

            gb.board.put(new Coordinates(0,3),null);

            gb.board.put(new Coordinates(2,2),null);
            gb.board.put(new Coordinates(2,6),null);

            gb.board.put(new Coordinates(3,8),null);

            gb.board.put(new Coordinates(5,0),null);

            gb.board.put(new Coordinates(6,2),null);
            gb.board.put(new Coordinates(6,6),null);

            gb.board.put(new Coordinates(8,5),null);

        }

        if(persone > 3){

            gb.board.put(new Coordinates(0,4),null);

            gb.board.put(new Coordinates(1,5),null);

            gb.board.put(new Coordinates(3,1),null);

            gb.board.put(new Coordinates(4,0),null);
            gb.board.put(new Coordinates(4,8),null);

            gb.board.put(new Coordinates(5,7),null);

            gb.board.put(new Coordinates(7,3),null);

            gb.board.put(new Coordinates(8,4),null);

        }


        return gb;

    }

    public Set<Coordinates> getCoords(){
        return board.keySet();
    }

    public Tile getTile(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
        if(!board.containsKey(c)){
            throw InvalidCoordinatesForCurrentGameException;
        }

        return board.get(c);
    }

    public void setTile(Coordinates c, Tile t) throws InvalidCoordinatesForCurrentGameException{
        if(!board.containsKey(c)){
            throw InvalidCoordinatesForCurrentGameException;
        }

        board.put(c,t);
    }

    public Tile pickTile(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
        if(!board.containsKey(c)){
            throw InvalidCoordinatesForCurrentGameException;
        }

        Tile t = board.get(c);
        board.put(c,null);
        return t;
    }

    //To be moved in the controller???
    public int checkBoardGoal(Shelf s){

    }
}
