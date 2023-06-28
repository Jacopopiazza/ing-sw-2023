package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.ModelView.PlayerView;

/**
 * The Player class represents a player in the game.
 */

public class Player{
    private int score;                          // The score of the player
    private Shelf shelf;                        // The shelf of the player
    private PrivateGoal goal;                   // The private goal of the player
    private final String username;              // The username of the player
    private int[] accomplishedGlobalGoals;       // The accomplished global goals of the player
    private boolean winner;                      // Indicates if the player is the winner

    /**
     * Constructs a new {@code Player} object with the specified username.
     *
     * @param u the username of the player
     */
    public Player(String u) {
        username = u;
        score = 0;
        shelf = null;
        goal = null;
        accomplishedGlobalGoals = null;
        winner = false;
    }

    /**
     * Gets the ModelView representation of the player.
     *
     * @return a {@link PlayerView}
     */
    public PlayerView getView() {
        return new PlayerView(this);
    }

    /**
     * Initializes the player with a private goal.
     *
     * @param privateGoal the {@link PrivateGoal} for the player
     */
    public void init(PrivateGoal privateGoal) {
        shelf = new Shelf();
        goal = privateGoal;
        accomplishedGlobalGoals = new int[]{0, 0};
    }

    /**
     * Inserts the specified {@code Tiles} into the player's shelf column.
     *
     * @param t       the array of {@link Tile} to insert
     * @param column  the column index
     * @throws NoTileException                 if no tile is provided
     * @throws ColumnOutOfBoundsException     if the column index is out of bounds
     * @throws IllegalColumnInsertionException if the column insertion is illegal
     */
    public void insert(Tile t[], int column) throws NoTileException, ColumnOutOfBoundsException, IllegalColumnInsertionException {
        if( ( t == null ) || ( t.length == 0 ) ) {
            throw new NoTileException();
        }

        if(t.length > shelf.remainingSpaceInColumn(column)) throw new IllegalColumnInsertionException();

        for( int i = 0; ( i < t.length ) && ( t[i] != null ) ; i++) {
            shelf.addTile(t[i], column);
        }
    }

    /**
     * Gets the username of the player.
     *
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the accomplished global goals of the player.
     *
     * @return an array of accomplished global goals
     */
    public int[] getAccomplishedGlobalGoals() {
        return this.accomplishedGlobalGoals.clone();
    }

    /**
     * Sets the accomplished global goal at the specified index.
     *
     * @param i     the index
     * @param token the value to set
     * @throws InvalidIndexException if the index is invalid
     */
    public void setAccomplishedGlobalGoal( int i, int token ) throws InvalidIndexException {
        if( ( i < 0 ) || ( i >= this.accomplishedGlobalGoals.length ) ) throw new InvalidIndexException();
        this.accomplishedGlobalGoals[i] = token;
    }

    /**
     * Gets the score of the player.
     *
     * @return the score
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of the player.
     *
     * @param s the score to set
     * @throws InvalidScoreException if the score is invalid
     */
    public void setScore(int s) throws InvalidScoreException {
        if( s < 0 ) throw new InvalidScoreException();
        score = s;
    }

    /**
     * Gets a clone of the player's shelf.
     *
     * @return a clone of the player's shelf
     */
    public Shelf getShelf() {
        return shelf.clone();
    }

    /**
     * Sets the player's {@code Shelf}.
     *
     * @param shelf the {@link Shelf} to set
     * @throws MissingShelfException if the shelf is missing
     */
    public void setShelf(Shelf shelf) throws MissingShelfException{
        if( shelf == null ) throw new MissingShelfException();
        this.shelf = shelf.clone();
    }

    /**
     * Gets the {@code PrivateGoal} of the player.
     *
     * @return the {@link PrivateGoal}
     */
    public PrivateGoal getGoal() {
        return goal;
    }

    /**
     * Sets the {@code PrivateGoal} of the player.
     *
     * @param goal the {@link PrivateGoal} to set
     */
    public void setGoal(PrivateGoal goal) {
        this.goal = goal;
    }

    /**
     * Increases the player's score by 1.
     */
    public void first() {
        this.score++;
    }

    /**
     * Checks if the player is the winner.
     *
     * @return true if the player is the winner, false otherwise
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     * Sets the player as the winner.
     */
    public void setWinner() {
        this.winner = true;
    }

    /**
     * Gets the player's {@code PrivateGoal}.
     *
     * @return the {@link PrivateGoal}
     */
    public PrivateGoal getPrivateGoal() {
        return goal;
    }

    /**
     * Checks if the player's private goal is satisfied and updates the score accordingly.
     *
     * @return true if the private goal is correct, false otherwise
     * @throws MissingShelfException       if the shelf is missing
     * @throws ColumnOutOfBoundsException if the column index is out of bounds
     */
    public boolean checkPrivateGoal() throws MissingShelfException, ColumnOutOfBoundsException {
        int res = goal.check(shelf.clone());
        score += res;
        if( res > 0 ) return true;
        return false;
    }

}
