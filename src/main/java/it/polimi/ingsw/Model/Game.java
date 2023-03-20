package it.polimi.ingsw.Model;
import it.polimi.ingsw.Model.GlobalGoals.*;

import java.util.*;

public class Game {
    private GameBoard board;
    private Player[] players;
    private GlobalGoal[] goals;
    private int currentPlayer;
    private TileSack sack;


    public static final int maxNumberOfPlayers = 4;

    public Game(Player[] players) throws InvalidNumberOfPlayersException{

        if(players.size() < 2 || players.size() > maxNumberOfPlayers){
            throw InvalidNumberOfPlayersException;
        }

        this.players = players.clone();
        board = GameBoard.getGameBoard(this.players.size());
        sack = new TileSack();
        currentPlayer = new Random().nextInt(this.players.size());
        goals = pickTwoGlobalGoals();
    }

    public int getCurrentPlayer(){
        return currentPlayer;
    }

    public void setCurrentPlayer(int cp) throws InvalidIndexException{

        if(cp < 0 || cp >= players.length){
            throw new InvalidIndexException();
        }

        currentPlayer = cp;
    }

    public Player getPlayer(int p) throws InvalidIndexException{
        if(p < 0 || p >= players.length){
            throw new InvalidIndexException();
        }
        return players[p];
    }

    public boolean checkGlobalGoals(){

        boolean retValue = false;
        int currentScore = players[currentPlayer].getScore();

        for(int i=0;i<goals.length;i++){

            if(!players[currentPlayer].getGlobalGoalAccomplished()[i] && goals[i].check(players[currentPlayer].getShelf())){
                players[currentPlayer].setGlobalGoalAccomplishedTrue(i);
                currentScore += goals[i].popScore();
                retValue = true;
            }

        }

        players[currentPlayer].setScore(currentScore);
        return retValue;
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

    private static GlobalGoal[] pickTwoGlobalGoals(){
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
