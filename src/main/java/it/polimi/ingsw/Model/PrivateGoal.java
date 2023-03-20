package it.polimi.ingsw.Model;
import com.google.gson.Gson;
import it.polimi.ingsw.Exceptions.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

public final class PrivateGoal {
    private Coordinates[] coords;
    public PrivateGoal(Coordinates[] coords) {
        //Implementa con JSON
        /*
        //ToRead form JSON
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/PrivateGoals.json"));

        Coordinates[][] allPrivateGoals = gson.fromJson(reader, Coordinates[][].class);

        coords = allPrivateGoals[new Random().nextInt(allPrivateGoals.length)];
        */

        this.coords = coords.clone();

    }



    public int check(Shelf shelf) throws MissingShelfException, ColumnOutOfBoundsException{
        if(shelf==null) throw new MissingShelfException("Missing shelf");
        int numOfCorrectTiles=0;
        Tile temp;
        for(int i=0;i<coords.length;i++){
            temp=shelf.getTile(coords[i]);
            if(temp!=null && temp.getColor().ordinal()==i) numOfCorrectTiles++;
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
