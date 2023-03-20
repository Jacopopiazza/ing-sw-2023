package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;


public class EqualLines extends GlobalGoal {

    @Override
    public boolean check(Shelf s) throws MissingShelfException{
        if(s==null) throw new MissingShelfException();
        int r=s.getRows();
        int c=s.getColumns;
        int correctR=0;
        Tile temp;
        for(int i=0;i<r && correctR<4;i++){
            TileColor[] availableColors = new TileColor[3];
            int currentHead;
            temp=s.getTile(new Coordinates(i,0));
            if(temp!=null){
                availableColors[0]=temp.getColor();
                currentHead=1;
            }
            else currentHead=4; //doing this the check will be false for this row, because a tile is missing
            for(int j=1;j<c && currentHead<=3;j++){
                temp=s.getTile(new Coordinates(i,j));
                if(temp!=null){
                    TileColor tc=temp.getColor();
                    boolean present=false;
                    for(int k=0;k<currentHead && present==false;k++){
                        if(tc.equals(availableColors[k])) present=true;
                    }
                    if(present==false){
                        if(currentHead<3) availableColors[currentHead]=tc;
                        currentHead++;
                    }
                }
                else currentHead=4; //doing this the check will be false for this row, because a tile is missing
            }
            if(currentHead<=3) correctR++;
        }
        if(correctR==4) return true;
        return false;
    }
}
