package it.polimi.ingsw.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

public final class PrivateGoal {

    /*
    * Colors at given index in Coordinates
    * index 0 -> White
    * index 1 -> Fuchsia
    * index 2 -> Blue
    * index 3 -> Cyan
    * index 4 -> Yellow
    * index 5 -> Green
    * */
    private Coordinates[] coords;
    private PrivateGoal(Coordinates[] coords) {
        this.coords = coords.clone();
    }

    public Coordinates[] getCoordinates() {
        return coords;
    }

    public static PrivateGoal[] getPrivateGoals(int people) throws InvalidNumberOfPlayersException{

        if( people <= 0 || people > Config.getInstance().getMaxNumberOfPlayers()){
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

        Config config = Config.getInstance();
        final int numOfTiles = numOfCorrectTiles;

        return Arrays.stream(config.getPrivateGoals()).filter(g -> g.correctPosition() == numOfTiles).mapToInt(g -> g.score()).findFirst().getAsInt();

    }
}
