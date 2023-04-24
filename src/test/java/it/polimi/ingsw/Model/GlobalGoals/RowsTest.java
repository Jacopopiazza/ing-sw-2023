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

import static org.junit.Assert.*;

public class RowsTest extends TestCase {
    Shelf passShelfEqual;
    Shelf dontPassShelfEqual;
    Shelf passShelfDifferent;
    Shelf dontPassShelfDifferent;

    @Before
    public void setUp() throws NoTileException, IllegalColumnInsertionException {

        Gson gson = new Gson();
        TileColor[][] falseMatrix;
        TileColor[][] trueMatrix;

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/ShelfConfig.json"));
        JsonObject obj = gson.fromJson(reader , JsonObject.class);
        trueMatrix = gson.fromJson(obj.get("testTrueEqualRows"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseEqualRows"), TileColor[][].class);

        passShelfEqual = new Shelf();
        dontPassShelfEqual = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfEqual.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfEqual.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

        trueMatrix = gson.fromJson(obj.get("testTrueDifferentRows"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseDifferentRows"), TileColor[][].class);

        passShelfDifferent = new Shelf();
        dontPassShelfDifferent = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfDifferent.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfDifferent.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

    }
    @Test
    public void popScore() {
    }

    @Test
    public void testCheck() {

        Rows test = new Rows(2, true, 4, 3);
        assertTrue(test.check(passShelfEqual));
        assertFalse(test.check(dontPassShelfEqual));
        test = new Rows(2, false, 2, Shelf.getColumns());
        assertTrue(test.check(passShelfDifferent));
        assertFalse(test.check(dontPassShelfDifferent));
    }
}