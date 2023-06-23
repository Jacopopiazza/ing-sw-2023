package it.polimi.ingsw.Model.GlobalGoals;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.NoTileException;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.Tile;
import it.polimi.ingsw.Model.TileColor;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

public class ColumnsOrRowsTest extends TestCase {

    Shelf passShelfEqualColumns;
    Shelf dontPassShelfEqualColumns;
    Shelf passShelfDifferentColumns;
    Shelf dontPassShelfDifferentColumns;
    Shelf passShelfEqualRows;
    Shelf dontPassShelfEqualRows;
    Shelf passShelfDifferentRows;
    Shelf dontPassShelfDifferentRows;

    @Before
    public void setUp() throws NoTileException, IllegalColumnInsertionException {

        Gson gson = new Gson();
        TileColor[][] falseMatrix;
        TileColor[][] trueMatrix;

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/ShelfConfig.json"));
        JsonObject obj = gson.fromJson(reader , JsonObject.class);
        trueMatrix = gson.fromJson(obj.get("testTrueEqualColumns"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseEqualColumns"), TileColor[][].class);

        passShelfEqualColumns = new Shelf();
        dontPassShelfEqualColumns = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfEqualColumns.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfEqualColumns.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

        trueMatrix = gson.fromJson(obj.get("testTrueDifferentColumns"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseDifferentColumns"), TileColor[][].class);

        passShelfDifferentColumns = new Shelf();
        dontPassShelfDifferentColumns = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfDifferentColumns.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfDifferentColumns.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

        trueMatrix = gson.fromJson(obj.get("testTrueEqualRows"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseEqualRows"), TileColor[][].class);

        passShelfEqualRows = new Shelf();
        dontPassShelfEqualRows = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfEqualRows.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfEqualRows.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

        trueMatrix = gson.fromJson(obj.get("testTrueDifferentRows"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseDifferentRows"), TileColor[][].class);

        passShelfDifferentRows = new Shelf();
        dontPassShelfDifferentRows = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfDifferentRows.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfDifferentRows.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

    }
    @Test
    public void popScore() {
    }

    @Test
    public void testCheck() {

        ColumnsOrRows test = new ColumnsOrRows(2, true, true, 3, 3);
        assertTrue(test.check(passShelfEqualColumns));
        assertFalse(test.check(dontPassShelfEqualColumns));
        test = new ColumnsOrRows(2, false, true, 2, Shelf.getRows());
        assertTrue(test.check(passShelfDifferentColumns));
        assertFalse(test.check(dontPassShelfDifferentColumns));
        test = new ColumnsOrRows(2, true, false, 4, 3);
        assertTrue(test.check(passShelfEqualRows));
        assertFalse(test.check(dontPassShelfEqualRows));
        test = new ColumnsOrRows(2, false, false, 2, Shelf.getColumns());
        assertTrue(test.check(passShelfDifferentRows));
        assertFalse(test.check(dontPassShelfDifferentRows));
    }
}