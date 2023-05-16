package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.GlobalGoals.GlobalGoal;
import it.polimi.ingsw.Model.Utilities.Config;

import java.io.Serializable;
import java.util.Stack;

public class GameView implements Serializable {

    private final GameBoardView board;
    private final int numOfPlayers;
    private final PlayerView[] players;
    private final GlobalGoal[] goals;
    private final int currentPlayer;
    private final TileSackView sack;
    private final boolean started;
    private final boolean lastTurn;

    private final Stack<String> cheaters;

    public GameView(Game game) {
        this.board = game.getGameBoard().getView();
        this.numOfPlayers = game.getNumOfPlayers();
        this.players = new PlayerView[this.numOfPlayers];
        for(int i = 0; i < this.numOfPlayers; i++)
            this.players[i] = game.getPlayer(i).getView();
        this.currentPlayer = game.getCurrentPlayer();
        this.sack = game.getTileSack().getView();
        this.cheaters = game.getCheaters();
        this.goals = new GlobalGoal[Config.getInstance().getNumOfGlobalGoals()];
        this.started = game.isGameStarted();
        this.lastTurn = game.isLastTurn();
        try {
            for (int i = 0; i < game.getGoals().length; i++)
                this.goals[i] = game.getGoals()[i];
        } catch( CloneNotSupportedException e ){
            e.printStackTrace();
        }
    }

    public GameBoardView getGameBoard() {
        return board;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public GlobalGoal[] getGlobalGoals() { return goals; }

    public PlayerView[] getPlayers() {
        return players;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public TileSackView getTileSack() {
        return sack;
    }

    public Stack<String> getCheaters(){
        return cheaters;
    }

    public boolean isStarted(){ return started; }

    public boolean isLastTurn(){ return lastTurn; }
}
