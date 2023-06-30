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

public class GroupOfTilesTest extends TestCase {

    Shelf passShelfFour;
    Shelf dontPassShelfFour;
    Shelf passShelfTwo;
    Shelf dontPassShelfTwo;

    @Before
    public void setUp() throws NoTileException, IllegalColumnInsertionException {

        Gson gson = new Gson();
        TileColor[][] falseMatrix;
        TileColor[][] trueMatrix;

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/ShelfConfig.json"));
        JsonObject obj = gson.fromJson(reader , JsonObject.class);
        trueMatrix = gson.fromJson(obj.get("testTrueFourTiles"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseFourTiles"), TileColor[][].class);

        passShelfFour = new Shelf();
        dontPassShelfFour = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfFour.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfFour.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

        trueMatrix = gson.fromJson(obj.get("testTrueTwoTiles"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseTwoTiles"), TileColor[][].class);

        passShelfTwo = new Shelf();
        dontPassShelfTwo = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfTwo.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfTwo.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

    }

    @Test
    public void testCheck() {

        GroupOfTiles test = new GroupOfTiles(2,4,4);
        assertTrue(test.check(passShelfFour));
        assertFalse(test.check(dontPassShelfFour));
        test = new GroupOfTiles(2,2,6);
        assertTrue(test.check(passShelfTwo));
        assertFalse(test.check(dontPassShelfTwo));
    }
}