package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Model.PrivateGoal;

import java.io.Serializable;

/**
 * The {@code PlayerView} class represents the immutable version of the {@link it.polimi.ingsw.Model.Player}.
 * It provides a snapshot of the player's state in a serializable format.
 */
public class PlayerView implements Serializable {
    private static final long serialVersionUID=1L;
    private final int score;
    private final ShelfView shelf;
    private final PrivateGoalView privateGoal;
    private final String username;
    private final boolean winner;

    /**
     * Constructs a new {@code PlayerView} object based on the given {@link it.polimi.ingsw.Model.Player} object.
     *
     * @param player the {@link it.polimi.ingsw.Model.Player} object to create the view from
     */

    public PlayerView(Player player){
        this.score = player.getScore();
        this.shelf = new ShelfView(player.getShelf());
        this.privateGoal = new PrivateGoalView(player.getPrivateGoal());
        this.username = player.getUsername();
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
     * @return the {@link it.polimi.ingsw.ModelView.ShelfView} object representing the player's shelf
     */

    public ShelfView getShelf() {
        return shelf;
    }

    /**
     * Retrieves the view of the player's private goal.
     *
     * @return the {@link it.polimi.ingsw.ModelView.PrivateGoalView} object representing the player's private goal
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
     * Checks if the player is a winner.
     *
     * @return true if the player is a winner, false otherwise
     */

    public boolean isWinner() {
        return winner;
    }
}
