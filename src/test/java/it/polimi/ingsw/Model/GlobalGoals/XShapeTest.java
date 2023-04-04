package it.polimi.ingsw.Model.GlobalGoals;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.*;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Random;

import static org.junit.Assert.*;

public class XShapeTest extends TestCase {

    Shelf passShelf;
    Shelf dontPassShelf;

    @Before
    public void setUp() throws NoTileException, IllegalColumnInsertionException {

        Gson gson = new Gson();
        TileColor[][] falseMatrix;
        TileColor[][] trueMatrix;

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/ShelfConfig.json"));
        JsonObject obj = gson.fromJson(reader , JsonObject.class);
        trueMatrix = gson.fromJson(obj.get("testTrueXShape"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseXShape"), TileColor[][].class);

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
    public void popScore(){
    }

    @Test
    public void testCheck() {

        XShape test = new XShape(2);
        assertTrue(test.check(passShelf));
        assertFalse(test.check(dontPassShelf));
    }
}