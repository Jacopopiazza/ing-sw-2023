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
    private Coordinates[] coords;

    private PrivateGoal(Coordinates[] coords) {
        this.coords = coords.clone();
    }

    public Coordinates[] getCoordinates() {
        return coords;
    }

    public static PrivateGoal[] getPrivateGoals(int people) throws InvalidNumberOfPlayersException {
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(PrivateGoal.class.getResourceAsStream("/PrivateGoals.json"));
        Type listOfMyClassObject = new TypeToken<ArrayList<Coordinates[]>>() {
        }.getType();
        List<Coordinates[]> allPrivateGoals = gson.fromJson(reader, listOfMyClassObject);
        PrivateGoal[] retValue = new PrivateGoal[people];

        if (people <= 0 || people > Config.getInstance().getMaxNumberOfPlayers()) {
            throw new InvalidNumberOfPlayersException();
        }

        Collections.shuffle(allPrivateGoals);

        for (int i = 0; i < people; i++) {
            retValue[i] = new PrivateGoal(allPrivateGoals.get(i));
        }

        return retValue;
    }


    public int check(Shelf shelf) throws MissingShelfException, ColumnOutOfBoundsException {
        int numOfCorrectTiles = 0;
        final int numOfTiles;

        if (shelf == null) {
            throw new MissingShelfException("Missing shelf");
        }

        for (int i = 0; i < coords.length; i++) {
            if ((shelf.getTile(coords[i]) != null) && (shelf.getTile(coords[i]).getColor().ordinal() == i))
                numOfCorrectTiles++;
        }
        Config config = Config.getInstance();
        numOfTiles = numOfCorrectTiles;

        return Arrays.stream(config.getPrivateGoals()).filter(g -> g.correctPosition() == numOfTiles).mapToInt(g -> g.score()).findFirst().getAsInt();

    }



}
