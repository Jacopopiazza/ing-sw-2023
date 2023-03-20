package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.GlobalGoals.*;

import java.util.*;

public class Game {
    private GameBoard board;
    private List<Player> players;
    private GlobalGoal[] goals;
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
        goals = pickTwoPublicGoals();
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

    private static GlobalGoal[] pickTwoPublicGoals(){
        List<GlobalGoal> goals = new ArrayList<GlobalGoal>();

        goals.add( new Angles() );
        goals.add( new Couples() );
        goals.add( new Diagonal() );
        goals.add( new DifferentColumns() );
        goals.add( new DifferentLines() );
        goals.add( new EightTiles() );
        goals.add( new EqualColumns() );
        goals.add( new EqualLines() );
        goals.add( new FourTiles() );
        goals.add( new Square() );
        goals.add( new Stair() );
        goals.add( new XShape() );

        Collections.shuffle(goals);

        GlobalGoal[] returned = new GlobalGoal[2];
        for(int i = 0; i < returned.length; i++){
            returned[i] = goals.get(i);
        }

        return returned;

    }
}
