package it.polimi.ingsw.Model.PublicGoals;

import it.polimi.ingsw.Model.PublicGoal;
import it.polimi.ingsw.Model.Shelf;

public class FourTiles extends PublicGoal {

    @Override
    public boolean check(Shelf s) throws MissingShelfException {
        if(s==NULL) throw new MissingShelfException;
        int r=s.getRows();
        int c=s.getColumns;
        boolean[][] checked= new boolean[r][c];
        for(int i=0;i<r;i++){
            for(int j=0;j<c;j++){
                checked[i][j]=false;
            }
        }
        int currentG=0;


        for(int i=0;i<r && currentG<4;i++){
            for(int j=0;j<c && currentG<4;j++){
                if(checked[i][j]==false){
                    if(checkFromThisTile(s,new Coordinates(i,j),c,r,checked)>=4) currentG++;
                }
            }
        }
        if(currentG==4) return true;
        return false;
    }

    private int checkFromThisTile(Shelf s,Coordinates coord, int c, int r, boolean[][] checked){
        Tile t=s.getTile(coord);
        int i=coord.getX();
        int j=coord.getY();
        checked[i][j]=true;
        if(t==NULL) return 0;
        int res=1;
        Tile temp;

        //checking the Tile above this one
        if(i>0 && checked[i-1][j]==false){
            temp=s.getTile(new Coordinates(i-1,j));
            if(temp!=NULL && temp.getColor().equals(t.getColor())) res+=checkFromThisTile(s,new Coordinates(i-1,j),c,r,checked);
        }

        //checking the Tile under this one
        if(i<r-1 && checked[i+1][j]==false){
            temp=s.getTile(new Coordinates(i+1,j));
            if(temp!=NULL && temp.getColor().equals(t.getColor())) res+=checkFromThisTile(s,new Coordinates(i+1,j),c,r,checked);
        }

        //checking the Tile to the left of this one
        if(j>0 && checked[i][j-1]==false){
            temp=s.getTile(new Coordinates(i,j-1));
            if(temp!=NULL && temp.getColor().equals(t.getColor())) res+=checkFromThisTile(s,new Coordinates(i,j-1),c,r,checked);
        }

        //checking the Tile to the right of this one
        if(j<c-1 && checked[i][j+1]==false){
            temp=s.getTile(new Coordinates(i,j+1));
            if(temp!=NULL && temp.getColor().equals(t.getColor())) res+=checkFromThisTile(s,new Coordinates(i,j+1),c,r,checked);
        }

        return res;
    }
}
