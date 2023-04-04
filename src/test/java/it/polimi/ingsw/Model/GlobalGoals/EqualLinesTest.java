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

public class EqualLinesTest extends TestCase {
    Shelf passShelf;
    Shelf dontPassShelf;

    @Before
    public void setUp() throws NoTileException, IllegalColumnInsertionException {

        Gson gson = new Gson();
        TileColor[][] falseMatrix;
        TileColor[][] trueMatrix;

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/ShelfConfig.json"));
        JsonObject obj = gson.fromJson(reader , JsonObject.class);
        trueMatrix = gson.fromJson(obj.get("testTrueEqualLines"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseEqualLines"), TileColor[][].class);

        passShelf = new Shelf();
        dontPassShelf = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelf.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelf.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

    }
    @Test
    public void popScore() {
    }

    @Test
    public void testCheck() {

        EqualLines test = new EqualLines(2);
        assertTrue(test.check(passShelf));
        assertFalse(test.check(dontPassShelf));
    }
}