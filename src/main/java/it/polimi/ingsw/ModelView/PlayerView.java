package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Player;

import java.io.Serial;
import java.io.Serializable;

/**
 * The {@code PlayerView} class represents the immutable version of the {@link it.polimi.ingsw.Model.Player}.
 * It provides a snapshot of the player's state in a serializable format.
 */
public class PlayerView implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;

    /**
     * Current player's score.
     */
    private final int score;

    /**
     * Reference to the player's shelf.
     */
    private final ShelfView shelf;

    /**
     * Reference to the PrivateGoal.
     */
    private final PrivateGoalView privateGoal;

    /**
     * Player's username.
     */
    private final String username;

    /**
     * Tells if the player was the first to fill its shelf.
     */
    private final boolean winner;

    /**
     * Constructs a new {@code PlayerView} object based on the given {@code Player} object.
     *
     * @param player the {@link Player} object to create the view from
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
     * @return the {@link ShelfView} object representing the player's shelf
     */

    public ShelfView getShelf() {
        return shelf;
    }

    /**
     * Retrieves the view of the player's private goal.
     *
     * @return the {@link PrivateGoalView} object representing the player's private goal
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
