package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;

public class Player{
    private int score;
    private Shelf shelf;
    private PrivateGoal goal;
    private final String nickname;
    private int[] accomplishedGlobalGoals;
    private boolean winner;

    public Player(String nick) {
        nickname = nick;
        score = 0;
        shelf = null;
        goal = null;
        accomplishedGlobalGoals = null;
        winner = false;
    }

    public void init(PrivateGoal privateGoal) {
        score = 0;
        shelf = new Shelf();
        goal = privateGoal;
        accomplishedGlobalGoals = new int[]{0, 0};
        winner = false;
    }

    public void insert(Tile t[], int column) throws NoTileException, ColumnOutOfBoundsException, IllegalColumnInsertionException {
        if ( ( t == null ) || ( t.length == 0 ) ){
            throw new NoTileException();
        }

        if(t.length > shelf.remainingSpaceInColumn(column)) throw new IllegalColumnInsertionException();

        for ( int i = 0; ( i < t.length ) && ( t[i] != null ) ; i++){
            shelf.addTile(t[i], column);
        }
    }

    public String getNickname() {
        return nickname;
    }

    public int[] getAccomplishedGlobalGoals() {
        return this.accomplishedGlobalGoals.clone();
    }

    public void setAccomplishedGlobalGoal( int i, int token ) throws InvalidIndexException {
        if ( ( i < 0 ) || ( i >= this.accomplishedGlobalGoals.length ) ) throw new InvalidIndexException();
        this.accomplishedGlobalGoals[i] = token;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int s) throws InvalidScoreException {
        if ( s < 0 ) throw new InvalidScoreException();
        score = s;
    }

    public Shelf getShelf() {
        return shelf.clone();
    }

    //Method added just for testing purposes
    public void setShelf(Shelf shelf) throws MissingShelfException{

        if( shelf == null ) throw new MissingShelfException();

        this.shelf = shelf.clone();
    }

    public PrivateGoal getGoal() {
        return goal;
    }

    public void setGoal(PrivateGoal goal) {
        this.goal = goal;
    }

    public void first() {
        this.score++;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public PrivateGoal getPrivateGoal() {
        return goal;
    }

    public boolean checkPrivateGoal() throws MissingShelfException, ColumnOutOfBoundsException {
        int res = goal.check(shelf.clone());
        score += res;
        if ( res > 0 ) return true;
        return false;
    }

}
