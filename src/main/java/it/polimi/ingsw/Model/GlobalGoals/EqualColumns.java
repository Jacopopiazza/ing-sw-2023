package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;


public class EqualColumns extends GlobalGoal {

    @Override
    public boolean check(Shelf s) throws MissingShelfException, ColumnOutOfBoundsException {
        if(s==null) throw new MissingShelfException();
        int r=s.getRows();
        int c=s.getColumns();
        int correctC=0;
        Tile temp;
        for(int i=0;i<c && correctC<3;i++){
            TileColor[] availableColors = new TileColor[3];
            int currentHead;
            temp=s.getTile(new Coordinates(0,i));
            if(temp!=null){
                availableColors[0]=temp.getColor();
                currentHead=1;
            }
            else currentHead=4; //doing this the check will be false for this column, because at least one tile is missing
            for(int j=1;j<r && currentHead<=3;j++){
                    TileColor tc=s.getTile(new Coordinates(j,i)).getColor();
                    boolean present=false;
                    for(int k=0;k<currentHead && present==false;k++){
                        if(tc.equals(availableColors[k])) present=true;
                    }
                    if(present==false){
                        if(currentHead<3) availableColors[currentHead]=tc;
                        currentHead++;
                    }
            }
            if(currentHead<=3) correctC++;
        }
        if(correctC==3) return true;
        return false;
    }
}
