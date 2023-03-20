package it.polimi.ingsw.Model;

public final class PrivateGoal {
    private Coordinates[] coords;

    public PrivateGoal(Coordinates[] coords) {  //coords parameter?

    }

    public int check(Shelf shelf) throws MissingShelfException{
        if(shelf==NULL) throw new MissingShelfException();
        int numOfCorrectTiles=0;
        Tile temp;
        for(int i=0;i<coords.length;i++){
            temp=shelf.getTile(coords[i]);
            if(temp!=NULL && temp.getColor().ordinal()==i) numOfCorrectTiles++;
        }

        switch (numOfCorrectTiles){
            case 0: return 0;
            case 1: return 1;
            case 2: return 2;
            case 3: return 4;
            case 4: return 6;
            case 5: return 9;
            default: return 12;
        }
    }
}
