package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.GlobalGoals.GlobalGoal;
import it.polimi.ingsw.Model.Utilities.Config;

import java.io.Serializable;
import java.util.Stack;

public class GameView implements Serializable {

    private final GameBoardView board;
    private final Integer numOfPlayers;
    private final Integer numOfActivePlayers;
    private final PlayerView[] players;
    private final GlobalGoalView[] goals;
    private final Integer currentPlayer;
    private final TileSackView sack;
    private final String cheater;

    public GameView(Game game) {
        if(game != null){
            this.board = game.getGameBoard().getView();
            this.numOfPlayers = game.getNumOfPlayers();
            this.players = new PlayerView[this.numOfPlayers];
            for(int i = 0; i < this.numOfPlayers; i++)
                this.players[i] = game.getPlayer(i).getView();
            this.currentPlayer = game.getCurrentPlayer();
            this.numOfActivePlayers = game.getNumOfActivePlayers();
            this.sack = game.getTileSack().getView();
            this.cheater = null;
            this.goals = new GlobalGoalView[Config.getInstance().getNumOfGlobalGoals()];
            try {
                for(int i = 0; i < game.getGoals().length; i++)
                    this.goals[i] = new GlobalGoalView(game.getGoals()[i]);
            } catch (CloneNotSupportedException e){
                e.printStackTrace();
            }
        }
        else{
            this.board = null;
            this.numOfPlayers = null;
            this.players = null;
            this.currentPlayer = null;
            this.numOfActivePlayers = null;
            this.sack = null;
            this.cheater = null;
            this.goals = null;
        }
    }

    public GameView(GameBoard board, TileSack sack) {
        if (board != null ) this.board = board.getView();
        else this.board = null;
        this.numOfPlayers = null;
        this.players = null;
        this.currentPlayer = null;
        this.numOfActivePlayers = null;
        if (sack != null ) this.sack = sack.getView();
        else this.sack = null;
        this.cheater = null;
        this.goals = null;
    }

    public GameView(Player[] players) {
        this.board = null;
        this.numOfPlayers = null;
        if(players != null) {
            this.players = new PlayerView[players.length];
            for(int i = 0; i < players.length; i++){
                this.players[i] = new PlayerView(players[i]);
            }
        }
        else this.players = null;
        this.currentPlayer = null;
        this.numOfActivePlayers = null;
        this.sack = null;
        this.cheater = null;
        this.goals = null;
    }

    public GameView(Player p, int index, GlobalGoal[] goals) {
        this.board = null;
        this.numOfPlayers = null;
        if(index >= 0 && index <= Config.getInstance().getMaxNumberOfPlayers() && p !=null){
            this.players = new PlayerView[Config.getInstance().getMaxNumberOfPlayers()];
            players[index] = p.getView();
        }
        else this.players = null;
        this.currentPlayer = null;
        this.numOfActivePlayers = null;
        this.sack = null;
        this.cheater = null;
        if(goals != null){
            this.goals = new GlobalGoalView[goals.length];
            for(int i = 0; i < goals.length; i++)
                this.goals[i] = new GlobalGoalView(goals[i]);
        }
        else this.goals = null;
    }

    public GameView(String cheater) {
        this.board = null;
        this.numOfPlayers = null;
        this.players = null;
        this.currentPlayer = null;
        this.numOfActivePlayers = null;
        this.sack = null;
        this.cheater = cheater;
        this.goals = null;
    }

    public GameView(Integer currentPlayer, Integer numOfActivePlayers) {
        this.board = null;
        this.numOfPlayers = null;
        this.players = null;
        if(currentPlayer!=null && currentPlayer>=0 && currentPlayer<=Config.getInstance().getMaxNumberOfPlayers()) this.currentPlayer = currentPlayer;
        else this.currentPlayer = null;
        if(numOfActivePlayers!=null && numOfActivePlayers>=0 && numOfActivePlayers<=Config.getInstance().getMaxNumberOfPlayers()) this.numOfActivePlayers = numOfActivePlayers;
        else this.numOfActivePlayers = null;
        this.sack = null;
        this.cheater = null;
        this.goals = null;
    }

    public GameBoardView getGameBoard() {
        return board;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public GlobalGoalView[] getGlobalGoals() { return goals; }

    public PlayerView[] getPlayers() {
        return players;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public TileSackView getTileSack() {
        return sack;
    }

    public String getCheater(){
        return cheater;
    }
}
