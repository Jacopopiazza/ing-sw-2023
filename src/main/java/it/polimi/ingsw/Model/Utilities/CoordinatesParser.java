package it.polimi.ingsw.Model.Utilities;

import com.google.gson.JsonElement;
import it.polimi.ingsw.Model.Coordinates;

public class CoordinatesParser {

    private CoordinatesParser() {

    }

    public static Coordinates coordinatesParser(JsonElement jsonCoordinates) {
        int x = jsonCoordinates.getAsJsonObject().get("x").getAsInt();
        int y = jsonCoordinates.getAsJsonObject().get("y").getAsInt();

        return new Coordinates(x,y);
    }
}