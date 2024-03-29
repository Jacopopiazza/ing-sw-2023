package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.GlobalGoals.GlobalGoal;
import it.polimi.ingsw.Utilities.Config;

import java.io.Serializable;


/**
 * The {@code GameView} class represents the immutable version of the {@link it.polimi.ingsw.Model.Game}.
 * It provides a snapshot of the game state in a serializable format.
 */
public class GameView implements Serializable {

    /**
     * Reference to the GameBoardView.
     */
    private final GameBoardView board;

    /**
     * Number of active players.
     */
    private final Integer numOfActivePlayers;

    /**
     * Array of references to the PlayerViews.
     */
    private final PlayerView[] players;

    /**
     * Reference to the GlobalGoals.
     */
    private final GlobalGoalView[] goals;

    /**
     * Index of the current player.
     */
    private final Integer currentPlayer;

    /**
     * Username of the player who cheated.
     */
    private final String cheater;

    /**
     * Constructs a {@code GameView} object based on the provided {@code Game}.
     *
     * @param game the {@link Game} from which to create the view
     */
    public GameView(Game game) {
        if(game != null){
            this.board = game.getGameBoard().getView();
            this.players = new PlayerView[game.getNumOfPlayers()];
            for(int i = 0; i < game.getNumOfPlayers(); i++)
                this.players[i] = game.getPlayer(i).getView();
            this.currentPlayer = game.getCurrentPlayer();
            this.numOfActivePlayers = game.getNumOfActivePlayers();
            this.cheater = null;
            this.goals = new GlobalGoalView[Config.getInstance().getNumOfGlobalGoals()];
            try {
                for(int i = 0; i < game.getGoals().length; i++)
                    this.goals[i] = game.getGoals()[i].getView();
            } catch (CloneNotSupportedException ignored){
            }
        }
        else{
            this.board = null;
            this.players = null;
            this.currentPlayer = null;
            this.numOfActivePlayers = null;
            this.cheater = null;
            this.goals = null;
        }
    }

    /**
     * Constructs a {@code GameView} object based on the provided {@code GameBoard}.
     *
     * @param board the {@link GameBoard} to include in the view
     */
    public GameView(GameBoard board) {
        if (board != null ) this.board = board.getView();
        else this.board = null;
        this.players = null;
        this.currentPlayer = null;
        this.numOfActivePlayers = null;
        this.cheater = null;
        this.goals = null;
    }

    /**
     * Constructs a {@code GameView} object based on the provided {@code Player} array.
     *
     * @param players the array of {@link Player} to include in the view
     */
    public GameView(Player[] players) {
        this.board = null;
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
     * Constructs a {@code GameView} object based on the provided {@code Player}, index, and {@code GloablGoal} array.
     *
     * @param p     the {@link Player} to include in the view
     * @param index the index of the player in the view
     * @param goals the array of {@link GlobalGoal} to include in the view
     */
    public GameView(Player p, int index, GlobalGoal[] goals) {
        this.board = null;
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
     * Constructs a {@code GameView} object with a cheater string.
     *
     * @param cheater the cheater string to include in the view.
     * @param currentPlayer the current player.
     */
    public GameView(String cheater, int currentPlayer) {
        this.board = null;
        this.players = null;
        this.currentPlayer = currentPlayer;
        this.numOfActivePlayers = null;
        this.cheater = cheater;
        this.goals = null;
    }

    /**
     * Constructs a {@code GameView} object with the current player and number of active players.
     *
     * @param currentPlayer      the index of the current player
     * @param numOfActivePlayers the number of active players
     */
    public GameView(Integer currentPlayer, Integer numOfActivePlayers) {
        this.board = null;
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
     * @return the {@link GameBoardView}
     */
    public GameBoardView getGameBoard() {
        return board;
    }

    /**
     * Returns the array of global goal views.
     *
     * @return the array of {@link GlobalGoalView}
     */
    public GlobalGoalView[] getGlobalGoals() { return goals; }

    /**
     * Returns the array of player views.
     *
     * @return the array of {@link PlayerView}s.
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
