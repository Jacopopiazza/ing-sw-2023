package it.polimi.ingsw.Model;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Exceptions.*;

import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

public final class PrivateGoal {
    private Coordinates[] coords;
    private PrivateGoal(Coordinates[] coords) {
        this.coords = coords.clone();
    }

    public Coordinates[] getCoordinates() {
        return coords;
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

    private static class JSONConfig{

        private record PrivateGoalPoint(int correctPosition, int points) {

        }

        private PrivateGoalPoint[] privateGoals;

        public PrivateGoalPoint[] getPrivateGoals() {
            return privateGoals;
        }
    }

    public int check(Shelf shelf) throws MissingShelfException, ColumnOutOfBoundsException{
        if(shelf==null) throw new MissingShelfException("Missing shelf");
        int numOfCorrectTiles=0;
        Tile temp;
        for(int i=0;i<coords.length;i++){
            temp=shelf.getTile(coords[i]);
            if(temp!=null && temp.getColor().ordinal()==i) numOfCorrectTiles++;
        }

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(PrivateGoal.class.getResourceAsStream("/Config.json"));
        final int numOfTiles = numOfCorrectTiles;
        JSONConfig config = gson.fromJson(reader,JSONConfig.class);
        return Arrays.stream(config.getPrivateGoals()).filter(g -> g.correctPosition == numOfTiles).mapToInt(g -> g.points).findFirst().getAsInt();

    }
}
