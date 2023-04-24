package it.polimi.ingsw.Model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.InvalidCoordinatesForCurrentGameException;
import it.polimi.ingsw.Exceptions.MissingShelfException;
import it.polimi.ingsw.Exceptions.NoTileException;
import junit.framework.TestCase;
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


public class GameBoardTest extends TestCase {
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
        JsonArray array = gson.fromJson(reader, JsonArray.class);

        int peopleOfCurrentConfig;
        for( JsonElement elem : array ){
            JsonObject obj = elem.getAsJsonObject();

            peopleOfCurrentConfig = obj.get("people").getAsInt();

            if( peopleOfCurrentConfig > people ) continue;

            JsonArray jsonCells = obj.get("cells").getAsJsonArray();

            int r, c;
            for( JsonElement jsonCell : jsonCells ){
                r = jsonCell.getAsJsonObject().get("ROW").getAsInt();
                c = jsonCell.getAsJsonObject().get("COL").getAsInt();
                coordinatesSetToTest.add(new Coordinates(r,c));
            }

        }

        Assert.assertEquals(coordinatesSet.size(), coordinatesSetToTest.size());
        Assert.assertTrue(coordinatesSet.containsAll(coordinatesSetToTest));
        Assert.assertTrue(coordinatesSetToTest.containsAll(coordinatesSet));

        // Is this necessary?

        for(int i = 0; i < 9; i++){
            for(int k = 0; k < 9; k++){
                Coordinates c = new Coordinates(i, k);
                if(!coordinatesSet.contains(c))
                    //Assert.assertNull(gameBoard.getTile(c));
                    Assert.assertThrows(InvalidCoordinatesForCurrentGameException.class, () -> {
                        gameBoard.getTile(c);
                    });
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

    @Test
    public void testSetTile_ThrowsInvalidCoordinatesForCurrentGameException() {
        Coordinates coordToFail = new Coordinates(0, 0);
        Tile t = new Tile(TileColor.CYAN, 0);
        Assert.assertThrows(InvalidCoordinatesForCurrentGameException.class, () -> gameBoard.setTile(coordToFail, t));
    }

    @Test
    public void testToRefill() throws InvalidCoordinatesForCurrentGameException {
        assertTrue(gameBoard.toRefill());
        for ( Coordinates c: gameBoard.getCoords() ) {
            gameBoard.setTile(c,new Tile(TileColor.BLUE,0));
        }
        assertFalse(gameBoard.toRefill());
        boolean flag = true;
        for ( Coordinates c: gameBoard.getCoords() ) {
            if(!flag) gameBoard.setTile(c,null);
            if(flag) flag = false;
        }
        assertTrue(gameBoard.toRefill());
    }

    @Test
    public void testPickTile() {
        // Need to know how the board is composed
    }

    @Test
    public void testCheckBoardGoal_ThrowsMissingShelfException(){
        Assert.assertThrows(MissingShelfException.class, () -> { GameBoard.checkBoardGoal(null); });
    }

    @Test
    public void testCheckBoardGoal() throws IllegalColumnInsertionException, NoTileException {
        Shelf s = new Shelf();
        for( int i=0; i<Shelf.getColumns(); i++ ){
            for( int j=0; j<Shelf.getRows(); j++ ) {
                s.addTile(new Tile(TileColor.values()[i], 0), i);
            }
        };
        assertEquals(40, GameBoard.checkBoardGoal(s) );
    }
}