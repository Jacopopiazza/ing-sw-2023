package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Exceptions.EmptyStackException;
import it.polimi.ingsw.Model.GlobalGoals.*;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Game {
    private final GameBoard board;
    private final Player[] players;
    private final GlobalGoal[] goals;
    private int currentPlayer;
    private final TileSack sack;


    public static final int maxNumberOfPlayers = 4;

    public Game(String[] nicknames) throws InvalidNumberOfPlayersException{

        if(nicknames.length < 2 || nicknames.length > maxNumberOfPlayers){
            throw new InvalidNumberOfPlayersException();
        }

        PrivateGoal[] privateGoals = PrivateGoal.privateGoalsForNPeople(nicknames.length);
        this.players = new Player[nicknames.length];
        for(int i=0;i<players.length;i++){
            players[i] = new Player(privateGoals[i],nicknames[i]);
        }


        board = GameBoard.getGameBoard(this.players.length);
        sack = new TileSack();
        currentPlayer = new Random().nextInt(this.players.length);
        goals = this.pickTwoGlobalGoals();
    }

    public int getCurrentPlayer(){
        return currentPlayer;
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

    public boolean checkGlobalGoals() throws EmptyStackException, NonValidScoreException, InvalidIndexException {

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

    public TileSack getTileSack(){
        return sack;
    }

    public GameBoard getGameBoard(){
        return board;
    }

    private GlobalGoal[] pickTwoGlobalGoals(){
        List<GlobalGoal> goals = new ArrayList<GlobalGoal>();

        goals.add( new Angles() );
        goals.add( new Diagonal() );
        goals.add( new DifferentColumns() );
        goals.add( new DifferentLines() );
        goals.add( new EightTiles() );
        goals.add( new EqualColumns() );
        goals.add( new EqualLines() );
        goals.add( new FourTiles() );
        goals.add( new Square() );
        goals.add( new Stair() );
        goals.add( new TwoTiles() );
        goals.add( new XShape() );

        Collections.shuffle(goals);

        GlobalGoal[] returned = new GlobalGoal[2];
        for(int i = 0; i < returned.length; i++){
            returned[i] = goals.get(i);
        }

        return returned;

    }
}
