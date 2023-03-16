package it.polimi.ingsw.Model;

public class Player implements Cloneable{

    private int score;
    private Shelf shelf;
    private PrivateGoal goal;

    public Player(){
        score=0;
        shelf=new Shelf();
        goal=new PrivateGoal();
    }

    //private constructor made for the clone method
    private Player(int sc, Shelf s, PrivateGoal pg){
        score=sc,
        shelf=(Shelf)s.clone();
        goal=pg;
    }

    public void insert(Tile t[], int column) throws NoTilesException, ColumnOutOfBoundsException{
        if(t==NULL || t[0]==NULL) throw new NoTilesException();
        if(column<0 || column>shelf.getColumns()) throw new ColumnOutOfBoundsException();
        for(int i=0;i<t.length && t[i]!=NULL;i++) shelf.addTile(t[i],column);
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
        return new Player(this.score,this.shelf,this.goal);
    }
}
