package it.polimi.ingsw.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Exceptions.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public final class PrivateGoal {
    private Coordinates[] coords;
    private PrivateGoal(Coordinates[] coords) {

        this.coords = coords.clone();

    }

    public static PrivateGoal[] privateGoalsForNPeople(int people) throws InvalidNumberOfPlayersException{

        if( people <= 0 || people > Game.maxNumberOfPlayers){
            throw new InvalidNumberOfPlayersException();
        }

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(PrivateGoal.class.getResourceAsStream("/PrivateGoals.json"));
        Type listOfMyClassObject = new TypeToken<ArrayList<Coordinates[]>>() {}.getType();

        List<Coordinates[]> allPrivateGoals = gson.fromJson(reader, listOfMyClassObject);

        Collections.shuffle(allPrivateGoals);

        PrivateGoal[] retValue = new PrivateGoal[people];

        for(int i = 0;i < people; i++){
            retValue[i] = new PrivateGoal(allPrivateGoals.get(i));
        }

        return retValue;
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
