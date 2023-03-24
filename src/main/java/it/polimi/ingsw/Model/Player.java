package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;

public class Player{

    private int score;
    private Shelf shelf;
    private PrivateGoal goal;
    private final String nickname;
    private boolean[] achievedGlobalGoals;

    public Player(PrivateGoal privateGoal, String nick) {
        nickname=nick;
        score = 0;
        shelf = new Shelf();
        goal = privateGoal;
        achievedGlobalGoals = new boolean[]{false, false};
    }

    public void insert(Tile t[], int column) throws NoTilesException, ColumnOutOfBoundsException, IllegalColumnInsertionException {
        if (t == null || t[0] == null) throw new NoTilesException();
        for (int i = 0; i < t.length && t[i] != null; i++) shelf.addTile(t[i], column);
    }

    public boolean[] getAchievedGlobalGoals() {
        return this.achievedGlobalGoals.clone();
    }

    public void setAchievedGlobalGoal(int i) throws InvalidIndexException {
        if ( ( i < 0 ) || ( i >= this.achievedGlobalGoals.length ) ) throw new InvalidIndexException();
        this.achievedGlobalGoals[i] = true;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int s) throws NonValidScoreException {
        if ( s < 0 ) throw new NonValidScoreException();
        score = s;
    }

    public Shelf getShelf() {
        return (Shelf) shelf.clone();
    }

    public boolean privateGoalCheck() throws MissingShelfException, ColumnOutOfBoundsException {
        int res = goal.check((Shelf) shelf.clone());
        score += res;
        if (res > 0) return true;
        return false;
    }
}
