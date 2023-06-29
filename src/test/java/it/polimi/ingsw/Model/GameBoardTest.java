package it.polimi.ingsw.Model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Messages.Message;
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
    GameBoard gameBoard3;
    GameBoard gameBoard2;
    Game game2;

    @Before
    public void setUp() throws FileNotFoundException {
        gameBoard3 = new GameBoard(3);
        game2 = new Game(2);
        game2.addPlayer("Picci",(Message message)->{});
        game2.addPlayer("Roma",(Message message)->{});
        game2.init();
        gameBoard2 = game2.getGameBoard();
        try {
            game2.refillGameBoard();
        } catch (EmptySackException e) {}
    }

    @Test
    public void testGetCoords() throws InvalidCoordinatesForCurrentGameException {
        Set<Coordinates> coordinatesSet = gameBoard3.getCoords();
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

        for(int i = 0; i < 9; i++){
            for(int k = 0; k < 9; k++){
                Coordinates c = new Coordinates(i, k);
                if(!coordinatesSet.contains(c))
                    Assert.assertThrows(InvalidCoordinatesForCurrentGameException.class, () -> {
                        gameBoard3.getTile(c);
                    });
            }
        }
    }

    @Test
    public void testSetTile() throws InvalidCoordinatesForCurrentGameException {
        Tile t = new Tile(TileColor.CYAN, 0);
        Coordinates c = new Coordinates(4, 4);
        gameBoard3.setTile(c, t);
        Assert.assertEquals(gameBoard3.getTile(c), t);
    }

    @Test
    public void testSetTile_ThrowsInvalidCoordinatesForCurrentGameException() {
        Coordinates coordToFail = new Coordinates(0, 0);
        Tile t = new Tile(TileColor.CYAN, 0);
        Assert.assertThrows(InvalidCoordinatesForCurrentGameException.class, () -> gameBoard3.setTile(coordToFail, t));
    }

    @Test
    public void testToRefill() throws InvalidCoordinatesForCurrentGameException {
        assertTrue(gameBoard3.toRefill());
        for ( Coordinates c: gameBoard3.getCoords() ) {
            gameBoard3.setTile(c,new Tile(TileColor.BLUE,0));
        }
        assertFalse(gameBoard3.toRefill());
        boolean flag = true;
        for ( Coordinates c: gameBoard3.getCoords() ) {
            if(!flag) gameBoard3.setTile(c,null);
            if(flag) flag = false;
        }
        assertTrue(gameBoard3.toRefill());
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
        }
        assertEquals(40, GameBoard.checkBoardGoal(s) );
    }

    @Test
    public void testIsPickable_InvalidCoordinatesForCurrentGameException() throws InvalidCoordinatesForCurrentGameException {
        Assert.assertThrows(InvalidCoordinatesForCurrentGameException.class, () -> gameBoard3.isPickable(new Coordinates(100, 100)) );
    }

    @Test
    public void testCheckChosenTiles() {
        Coordinates[] moreThan3 = new Coordinates[4];
        Coordinates[] duplicates = new Coordinates[2];
        Coordinates[] rightCoords = new Coordinates[1];

        Coordinates c1 = new Coordinates(1, 3);

        duplicates[0] = c1;
        duplicates[1] = c1;

        rightCoords[0] = c1;

        assertFalse(gameBoard3.checkChosenTiles(moreThan3));
        assertFalse(gameBoard3.checkChosenTiles(duplicates));
        assertTrue(gameBoard3.checkChosenTiles(rightCoords));
    }
}