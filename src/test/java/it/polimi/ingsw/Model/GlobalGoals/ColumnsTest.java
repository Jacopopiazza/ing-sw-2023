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

public class ColumnsTest extends TestCase {

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
        trueMatrix = gson.fromJson(obj.get("testTrueEqualColumns"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseEqualColumns"), TileColor[][].class);

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

        trueMatrix = gson.fromJson(obj.get("testTrueDifferentColumns"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseDifferentColumns"), TileColor[][].class);

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

        Columns test = new Columns(2, true, 3, 3);
        assertTrue(test.check(passShelfEqual));
        assertFalse(test.check(dontPassShelfEqual));
        test = new Columns(2, false, 2, Shelf.getRows());
        assertTrue(test.check(passShelfDifferent));
        assertFalse(test.check(dontPassShelfDifferent));
    }
}