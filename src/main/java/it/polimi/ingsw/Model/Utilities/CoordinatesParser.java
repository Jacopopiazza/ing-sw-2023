package it.polimi.ingsw.Model.Utilities;

import com.google.gson.JsonElement;
import it.polimi.ingsw.Model.Coordinates;

public class CoordinatesParser {

    private CoordinatesParser() {

    }

    public static Coordinates coordinatesParser(JsonElement jsonCoordinates) {
        int r = jsonCoordinates.getAsJsonObject().get("r").getAsInt();
        int c = jsonCoordinates.getAsJsonObject().get("c").getAsInt();

        return new Coordinates(r,c);
    }
}