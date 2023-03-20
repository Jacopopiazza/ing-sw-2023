package it.polimi.ingsw.Model.*;

public class Player implements Cloneable{

    private int score;
    private Shelf shelf;
    private PrivateGoal goal;

    private boolean[] globalGoalAccomplished;

    public Player(){
        score=0;
        shelf=new Shelf();
        goal=new PrivateGoal();
        globalGoalAccomplished = {false, false};
    }

    //private constructor made for the clone method
    private Player(int sc, Shelf s, PrivateGoal pg, boolean[] globalGoalAccomplished){
        score=sc,
        shelf=(Shelf)s.clone();
        goal=pg;
        this.globalGoalAccomplished = globalGoalAccomplished.clone();
    }

    public void insert(Tile t[], int column) throws NoTilesException, ColumnOutOfBoundsException{
        if(t==null || t[0]==null) throw new NoTilesException();
        if(column<0 || column>shelf.getColumns()) throw new ColumnOutOfBoundsException();
        for(int i=0;i<t.length && t[i]!=null;i++) shelf.addTile(t[i],column);
    }

    public boolean[] getGlobalGoalAccomplished(){
        return this.globalGoalAccomplished.clone();
    }

    public void setGlobalGoalAccomplishedTrue(int i) throws InvalidIndexException {
        if(i < 0 || i >= this.globalGoalAccomplished.length){
            throw new InvalidIndexException();
        }

        this.globalGoalAccomplished[i] = true;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int s) throws NonValidScoreException{
        if(s<0) throw new NonValidScoreException();
        score=s;
    }

    public Shelf getShelf(){
        return (Shelf) shelf.clone();
    }

    public boolean privateGoalCheck(){
        int res=goal.check((Shelf)shelf.clone());
        score+=res;
        if(res>0) return true;
        return false;
    }

    @Override
    public Object clone(){
        return new Player(this.score,this.shelf,this.goal, this.globalGoalAccomplished);
    }
}
