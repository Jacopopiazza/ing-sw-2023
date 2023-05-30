package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.GlobalGoals.GlobalGoal;
import it.polimi.ingsw.Model.Utilities.Config;

import java.io.Serializable;
import java.util.Stack;


/**
 * The GameView class represents the view of the game in the model-view architecture.
 * It provides a simplified representation of the game state for the client-side view.
 */
public class GameView implements Serializable {

    private final GameBoardView board;
    private final Integer numOfPlayers;
    private final Integer numOfActivePlayers;
    private final PlayerView[] players;
    private final GlobalGoalView[] goals;
    private final Integer currentPlayer;
    private final String cheater;

    /**
     * Constructs a GameView object based on the provided Game.
     *
     * @param game the game from which to create the view
     */
    public GameView(Game game) {
        if(game != null){
            this.board = game.getGameBoard().getView();
            this.numOfPlayers = game.getNumOfPlayers();
            this.players = new PlayerView[this.numOfPlayers];
            for(int i = 0; i < this.numOfPlayers; i++)
                this.players[i] = game.getPlayer(i).getView();
            this.currentPlayer = game.getCurrentPlayer();
            this.numOfActivePlayers = game.getNumOfActivePlayers();
            this.cheater = null;
            this.goals = new GlobalGoalView[Config.getInstance().getNumOfGlobalGoals()];
            try {
                for(int i = 0; i < game.getGoals().length; i++)
                    this.goals[i] = game.getGoals()[i].getView();
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
            this.cheater = null;
            this.goals = null;
        }
    }

    /**
     * Constructs a GameView object based on the provided GameBoard.
     *
     * @param board the game board to include in the view
     */
    public GameView(GameBoard board) {
        if (board != null ) this.board = board.getView();
        else this.board = null;
        this.numOfPlayers = null;
        this.players = null;
        this.currentPlayer = null;
        this.numOfActivePlayers = null;
        this.cheater = null;
        this.goals = null;
    }

    /**
     * Constructs a GameView object based on the provided Player array.
     *
     * @param players the array of players to include in the view
     */
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
        this.cheater = null;
        this.goals = null;
    }

    /**
     * Constructs a GameView object based on the provided Player, index, and GlobalGoal array.
     *
     * @param p     the player to include in the view
     * @param index the index of the player in the view
     * @param goals the array of global goals to include in the view
     */
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
        this.cheater = null;
        if(goals != null){
            this.goals = new GlobalGoalView[goals.length];
            for(int i = 0; i < goals.length; i++)
                this.goals[i] = goals[i].getView();
        }
        else this.goals = null;
    }

    /**
     * Constructs a GameView object with a cheater string.
     *
     * @param cheater the cheater string to include in the view
     */
    public GameView(String cheater) {
        this.board = null;
        this.numOfPlayers = null;
        this.players = null;
        this.currentPlayer = null;
        this.numOfActivePlayers = null;
        this.cheater = cheater;
        this.goals = null;
    }

    /**
     * Constructs a GameView object with the current player and number of active players.
     *
     * @param currentPlayer      the index of the current player
     * @param numOfActivePlayers the number of active players
     */
    public GameView(Integer currentPlayer, Integer numOfActivePlayers) {
        this.board = null;
        this.numOfPlayers = null;
        this.players = null;
        if(currentPlayer!=null && currentPlayer>=0 && currentPlayer<=Config.getInstance().getMaxNumberOfPlayers()) this.currentPlayer = currentPlayer;
        else this.currentPlayer = null;
        if(numOfActivePlayers!=null && numOfActivePlayers>=0 && numOfActivePlayers<=Config.getInstance().getMaxNumberOfPlayers()) this.numOfActivePlayers = numOfActivePlayers;
        else this.numOfActivePlayers = null;
        this.cheater = null;
        this.goals = null;
    }

    /**
     * Returns the game board view.
     *
     * @return the game board view
     */
    public GameBoardView getGameBoard() {
        return board;
    }

    /**
     * Returns the number of players in the game.
     *
     * @return the number of players
     */
    public Integer getNumOfPlayers() {
        return numOfPlayers;
    }

    /**
     * Returns the array of global goal views.
     *
     * @return the array of global goal views
     */
    public GlobalGoalView[] getGlobalGoals() { return goals; }

    /**
     * Returns the array of player views.
     *
     * @return the array of player views
     */
    public PlayerView[] getPlayers() {
        return players;
    }

    /**
     * Returns the index of the current player.
     *
     * @return the index of the current player
     */
    public Integer getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the number of active players.
     *
     * @return the number of active players
     */
    public Integer getNumOfActivePlayers(){return numOfActivePlayers;}

    /**
     * Returns the cheater string.
     *
     * @return the cheater string
     */
    public String getCheater(){
        return cheater;
    }
}
