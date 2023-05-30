package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Model.PrivateGoal;

import java.io.Serializable;

/**
 * The PlayerView class represents the view of a player in the model-view architecture.
 * It provides a simplified representation of the player's state for the client-side view.
 */
public class PlayerView implements Serializable {
    private static final long serialVersionUID=1L;
    private final int score;
    private final ShelfView shelf;
    private final PrivateGoalView privateGoal;
    private final String username;
    private final int[] accomplishedGlobalGoals;
    private final boolean winner;

    /**
     * Constructs a new PlayerView object based on the given Player object.
     *
     * @param player the Player object to create the view from
     */

    public PlayerView(Player player){
        this.score = player.getScore();
        this.shelf = new ShelfView(player.getShelf());
        this.privateGoal = new PrivateGoalView(player.getPrivateGoal());
        this.username = player.getUsername();
        this.accomplishedGlobalGoals = player.getAccomplishedGlobalGoals().clone();
        this.winner = player.isWinner();
    }

    /**
     * Retrieves the score of the player.
     *
     * @return the player's score
     */
    public int getScore() {
        return score;
    }

    /**
     * Retrieves the view of the player's shelf.
     *
     * @return the ShelfView object representing the player's shelf
     */

    public ShelfView getShelf() {
        return shelf;
    }

    /**
     * Retrieves the view of the player's private goal.
     *
     * @return the PrivateGoalView object representing the player's private goal
     */

    public PrivateGoalView getPrivateGoal() {
        return privateGoal;
    }

    /**
     * Retrieves the username of the player.
     *
     * @return the player's username
     */

    public String getUsername() {
        return username;
    }

    /**
     * Retrieves the array of global goal IDs accomplished by the player.
     *
     * @return an array of global goal IDs
     */

    public int[] getAccomplishedGlobalGoals() {
        return accomplishedGlobalGoals;
    }

    /**
     * Checks if the player is a winner.
     *
     * @return true if the player is a winner, false otherwise
     */

    public boolean isWinner() {
        return winner;
    }
}
