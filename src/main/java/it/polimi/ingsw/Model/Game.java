package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Exceptions.EmptyStackException;
import it.polimi.ingsw.Model.Utilities.Config;

import java.lang.String;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    private final GameBoard board;
    private final Player[] players;
    private final GlobalGoal[] goals;
    private int currentPlayer;
    private final TileSack sack;

    public Game(String[] nicknames) throws InvalidNumberOfPlayersException{
        PrivateGoal[] privateGoals = PrivateGoal.getPrivateGoals(nicknames.length);

        if( ( nicknames.length < 2 ) || ( nicknames.length > Config.getInstance().getMaxNumberOfPlayers() ) ){
            throw new InvalidNumberOfPlayersException();
        }

        this.players = new Player[nicknames.length];
        for( int i=0; i<players.length; i++)
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
        for( int i=0; i<this.goals.length; i++ )
            temp[i] = (GlobalGoal) goals[i].clone();
        return temp;
    }

    public void nextPlayer(){
        currentPlayer = (currentPlayer+1)%players.length;
    }

    public Player getPlayer(int p) throws InvalidIndexException{
        if(p < 0 || p >= players.length){
            throw new InvalidIndexException();
        }
        return players[p];
    }

    public boolean checkGlobalGoals() throws EmptyStackException, NonValidScoreException, InvalidIndexException, MissingShelfException {

        boolean retValue = false;
        int currentScore = players[currentPlayer].getScore();

        for(int i=0;i<goals.length;i++){

            if(!players[currentPlayer].getAchievedGlobalGoals()[i] && goals[i].check(players[currentPlayer].getShelf())){
                players[currentPlayer].setAchievedGlobalGoal(i);
                currentScore += goals[i].popScore();
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

    private GlobalGoal[] pickTwoGlobalGoals() throws InvalidNumberOfPlayersException {
        List<GlobalGoal> goals = GlobalGoal.getOneForEachChild(players.length);

        Collections.shuffle(goals);
        GlobalGoal[] returned = new GlobalGoal[2];
        for(int i = 0; i < returned.length; i++){
            returned[i] = goals.get(i);
        }

        return returned;

    }
}
