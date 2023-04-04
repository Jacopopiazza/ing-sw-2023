package it.polimi.ingsw.Model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.Exceptions.InvalidCoordinatesForCurrentGameException;
import it.polimi.ingsw.Exceptions.MissingShelfException;
import it.polimi.ingsw.Model.Utilities.CoordinatesParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;


public class GameBoardTest{

    GameBoard gameBoard;

    @Before
    public void setUp() throws FileNotFoundException {
        // open the file and initiate the gameBoard
        // assert all the unwanted Tiles are null
        gameBoard = new GameBoard(3);
    }

    @Test
    public void testGetCoords() throws InvalidCoordinatesForCurrentGameException {
        Set<Coordinates> coordinatesSet = gameBoard.getCoords();
        List<Coordinates> coordinatesSetToTest = new ArrayList<>(coordinatesSet.size());
        int people = 3;

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(Objects.requireNonNull(this.getClass().getResourceAsStream("/GameBoard.json")));
        JsonArray array;
        int peopleOfCurrentConfig;
        array = gson.fromJson(reader, JsonArray.class);

        for( JsonElement elem : array ){
            JsonObject obj = elem.getAsJsonObject();

            peopleOfCurrentConfig = obj.get("people").getAsInt();

            if( peopleOfCurrentConfig > people ) continue;

            JsonArray jsonCells = obj.get("cells").getAsJsonArray();

            for( JsonElement jsonCell : jsonCells ){
                Coordinates c = CoordinatesParser.coordinatesParser(jsonCell);
                coordinatesSetToTest.add(c);
            }

        }

        Assert.assertEquals(coordinatesSet.size(), coordinatesSetToTest.size());
        Assert.assertTrue(coordinatesSet.containsAll(coordinatesSetToTest));
        Assert.assertTrue(coordinatesSetToTest.containsAll(coordinatesSet));

        // Is this necessary?
        Coordinates c;
        for(int i = 0; i < 9; i++){
            for(int k = 0; k < 9; k++){
                c = new Coordinates(i, k);
                if(!coordinatesSet.contains(c))
                    Assert.assertNull(gameBoard.getTile(c));
            }
        }
    }

    @Test
    public void testSetTile() throws InvalidCoordinatesForCurrentGameException {
        Tile t = new Tile(TileColor.CYAN, 0);
        Coordinates c = new Coordinates(0, 1);
        gameBoard.setTile(c, t);
        Assert.assertEquals(gameBoard.getTile(c), t);
    }

    @Test (expected = InvalidCoordinatesForCurrentGameException.class)
    public void testSetTile_ThrowsInvalidCoordinatesForCurrentGameException() {
        Coordinates coordToFail = new Coordinates(0, 0);
        Tile t = new Tile(TileColor.CYAN, 0);
        Assert.assertThrows(InvalidCoordinatesForCurrentGameException.class, () -> gameBoard.setTile(coordToFail, t));
    }

    @Test
    public void testToRefill(){
        // Need to know how the board is composed
    }

    @Test
    public void testPickTile() {
        // Need to know how the board is composed
    }

    @Test (expected = MissingShelfException.class)
    public void testCheckBoardGoal_ThrowsMissingShelfException(){
        Assert.assertThrows(MissingShelfException.class, () -> GameBoard.checkBoardGoal(null));
    }

    @Test
    public void testCheckBoardGoal() {

    }
}