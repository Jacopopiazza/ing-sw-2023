package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;

import java.lang.String;
import java.util.*;

public class Game {
    private final GameBoard board;
    private final Player[] players;
    private final GlobalGoal[] goals;
    private int currentPlayer;
    private final TileSack sack;

    public Game(String[] nicknames) throws InvalidNumberOfPlayersException{
        if( ( nicknames.length < 2 ) || ( nicknames.length > Config.getInstance().getMaxNumberOfPlayers() ) ){
            throw new InvalidNumberOfPlayersException();
        }

        PrivateGoal[] privateGoals = PrivateGoal.getPrivateGoals(nicknames.length);

        this.players = new Player[nicknames.length];
        for( int i = 0; i < players.length; i++ )
            players[i] = new Player(privateGoals[i],nicknames[i]);

        board = new GameBoard(players.length);
        sack = new TileSack();
        currentPlayer = new Random().nextInt(this.players.length);
        goals = this.pickTwoGlobalGoals();
    }

    public int getCurrentPlayer(){
        return currentPlayer;
    }

    public GlobalGoal[] getGoals() throws CloneNotSupportedException {
        GlobalGoal[] temp = new GlobalGoal[this.goals.length];
        for( int i = 0; i < this.goals.length; i++ )
            temp[i] = goals[i].clone();
        return temp;
    }

    public void nextPlayer(){
        currentPlayer = (currentPlayer+1) % players.length;
    }

    public Player getPlayer(int p) throws InvalidIndexException{
        if( ( p < 0 ) || ( p >= players.length ) ){
            throw new InvalidIndexException();
        }
        return players[p];
    }

    public boolean checkGlobalGoals() throws EmptyStackException, InvalidScoreException, InvalidIndexException, MissingShelfException {
        boolean retValue = false;
        int token;
        int currentScore = players[currentPlayer].getScore();

        for( int i = 0; i < goals.length; i++ ){
            if( ( players[currentPlayer].getAccomplishedGlobalGoals()[i] == 0 ) && goals[i].check(players[currentPlayer].getShelf()) ){
                token = goals[i].popScore();
                players[currentPlayer].setAccomplishedGlobalGoal(i, token);
                currentScore += token;
                retValue = true;
            }

        }

        players[currentPlayer].setScore(currentScore);
        return retValue;
    }

    public TileSack getTileSack(){
        return sack;
    }

    public GameBoard getGameBoard(){
        return board;
    }

    public boolean refillGameBoard(){
        if( !this.board.toRefill() )
            return false;

        boolean noTileAdded = true;
        int remain[] = new int[TileColor.values().length];

        // For each Coordinate in the board
        for( Coordinates c : this.board.getCoords() ){
            // Check if the sack is empty
            if( ( Arrays.stream(this.sack.getRemaining()).sum() == 0 ) && noTileAdded )
                return false;
            else if( Arrays.stream(this.sack.getRemaining()).sum() == 0 )
                break;

            // Add a new Tile from the sack to the board
            try{
                if( this.board.getTile(c) == null ) {
                    this.board.setTile(c, this.sack.pop());
                    if( noTileAdded ) noTileAdded = false;
                }
            }
            catch( InvalidCoordinatesForCurrentGameException e ){}
        }
        // at least one Tile was added
        return true;
    }

    private GlobalGoal[] pickTwoGlobalGoals() throws InvalidNumberOfPlayersException {
        List<GlobalGoal> goals = GlobalGoal.getOneForEachChild(players.length);

        Collections.shuffle(goals);
        GlobalGoal[] returned = new GlobalGoal[2];
        for( int i = 0; i < returned.length; i++ ){
            returned[i] = goals.get(i);
        }

        return returned;
    }

}
