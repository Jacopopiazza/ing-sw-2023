package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Player;
import it.polimi.ingsw.Model.PrivateGoal;

import java.io.Serializable;

public class PlayerView implements Serializable {
    private static final long serialVersionUID=1L;
    private final int score;
    private final ShelfView shelf;
    private final PrivateGoal privateGoal;
    private final String username;
    private final int[] accomplishedGlobalGoals;
    private final boolean winner;

    public PlayerView(Player player){
        this.score = player.getScore();
        this.shelf = new ShelfView(player.getShelf());
        this.privateGoal = player.getPrivateGoal();
        this.username = player.getUsername();
        this.accomplishedGlobalGoals = player.getAccomplishedGlobalGoals().clone();
        this.winner = player.isWinner();
    }

    public int getScore() {
        return score;
    }

    public ShelfView getShelf() {
        return shelf;
    }

    public PrivateGoal getPrivateGoal() {
        return privateGoal;
    }

    public String getUsername() {
        return username;
    }

    public int[] getAccomplishedGlobalGoals() {
        return accomplishedGlobalGoals;
    }

    public boolean isWinner() {
        return winner;
    }
}
