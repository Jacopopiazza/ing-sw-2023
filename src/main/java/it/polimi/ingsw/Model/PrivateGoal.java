package it.polimi.ingsw.Model;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;

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

    public static PrivateGoal[] getPrivateGoals(int people) throws InvalidNumberOfPlayersException {
        if( ( people <= 0 ) || ( people > Config.getInstance().getMaxNumberOfPlayers() ) ){
            throw new InvalidNumberOfPlayersException();
        }

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(PrivateGoal.class.getResourceAsStream("/PrivateGoals.json"));

        JsonArray baseArray = gson.fromJson(reader, JsonArray.class);
        List<Coordinates[]> allPrivateGoals = new ArrayList<Coordinates[]>();

        for (JsonElement jsonPrivateGaol : baseArray) {
            JsonArray prvGoal = jsonPrivateGaol.getAsJsonArray();
            Coordinates[] coords = gson.fromJson(prvGoal, Coordinates[].class);
            allPrivateGoals.add(coords);
        }

        PrivateGoal[] retValue = new PrivateGoal[people];

        Collections.shuffle(allPrivateGoals);

        for ( int i = 0; i < people; i++ ) {
            retValue[i] = new PrivateGoal(allPrivateGoals.get(i));
        }

        return retValue;
    }


    public int check(Shelf shelf) throws MissingShelfException, ColumnOutOfBoundsException {
        if( shelf == null ) {
            throw new MissingShelfException("Missing shelf");
        }

        int numOfCorrectTiles = 0;
        final int numOfTiles;

        for( int i = 0; i < coords.length; i++ ){
            if( (shelf.getTile(coords[i]) != null) && (shelf.getTile(coords[i]).getColor().ordinal() == i ) )
                numOfCorrectTiles++;
        }
        Config config = Config.getInstance();
        numOfTiles = numOfCorrectTiles;

        return Arrays.stream(config.getPrivateGoals()).filter(g -> g.correctPosition() == numOfTiles).mapToInt(g -> g.score()).findFirst().getAsInt();
    }

}
