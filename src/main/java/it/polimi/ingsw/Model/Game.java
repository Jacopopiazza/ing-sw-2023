package it.polimi.ingsw.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Game {
    private GameBoard board;
    private List<Player> players;
    private PublicGoal[] goals;
    private int currentPlayer;
    private TileSack sack;

    public static final int maxNumberOfPlayers = 4;

    public Game() {
        board = null;
        sack = null;
        goals = null;
        players = new ArrayList<Player>();
        currentPlayer = -1;
    }

    public void newGame() throws InvalidNumberOfPlayersException{
        if(players.size() < 2 || players.size() > maxNumberOfPlayers){
            throw InvalidNumberOfPlayersException;
        }

        board = GameBoard.getGameBoard(players.size());
        sack = new TileSack();
        currentPlayer = new Random().nextInt(players.size());
    }

    public int getCurrentPlayer(){
        return currentPlayer;
    }

    public void setCurrentPlayer(int cp){
        currentPlayer = cp;
    }

    public Player getPlayer(int p){
        return players.get(p);
    }

    public void addPlayer(Player p) throws MaximumPlayersInGameException{
        if (players.size() == maxNumberOfPlayers){
            return MaximumPlayersInGameException;
        }

        players.add(p);
    }

    public boolean checkPublicGoals(){

    }

    public Tile popFromSack(){
        return sack.pop();
    }

    public Set<Coordinates> getCoordsFromBoard(){
        return board.getCoords();
    }

    public Tile getTileFromBoard(Coordinates c) throws InvalidCoordinatesForCurrentGameException {
        return board.getTile(c);
    }

    public void setTileOnBoard(Coordinates c, Tile t) throws InvalidCoordinatesForCurrentGameException{
        board.setTile(c,t);
    }

    public Tile pickTileFromBoard(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
        return board.pickTile(c);
    }

    private PublicGoal[] pickTwoPublicGoals(){

    }
}
