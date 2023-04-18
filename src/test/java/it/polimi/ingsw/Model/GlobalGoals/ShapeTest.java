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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.*;

public class ShapeTest extends TestCase {

    Shelf passShelfXShape;
    Shelf dontPassShelfXShape;
    Shelf passShelfDiagonal;
    Shelf dontPassShelfDiagonal;

    @Before
    public void setUp() throws NoTileException, IllegalColumnInsertionException {

        Gson gson = new Gson();
        TileColor[][] falseMatrix;
        TileColor[][] trueMatrix;

        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/ShelfConfig.json"));
        JsonObject obj = gson.fromJson(reader , JsonObject.class);
        trueMatrix = gson.fromJson(obj.get("testTrueXShape"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseXShape"), TileColor[][].class);

        passShelfXShape = new Shelf();
        dontPassShelfXShape = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfXShape.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfXShape.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

        trueMatrix = gson.fromJson(obj.get("testTrueDiagonal"), TileColor[][].class);
        falseMatrix = gson.fromJson(obj.get("testFalseDiagonal"), TileColor[][].class);

        passShelfDiagonal = new Shelf();
        dontPassShelfDiagonal = new Shelf();

        for(int i = trueMatrix.length-1; i >= 0;i--){
            for(int j = 0; j < trueMatrix[i].length; j++){
                if(trueMatrix[i][j] != null){
                    passShelfDiagonal.addTile(new Tile(trueMatrix[i][j],new Random(150).nextInt()), j);
                }
                if(falseMatrix[i][j] != null){
                    dontPassShelfDiagonal.addTile(new Tile(falseMatrix[i][j],new Random(160).nextInt()), j);
                }
            }
        }

    }

    @Test
    public void popScore(){
    }

    @Test
    public void testCheck() {

        List<List<Coordinates>> diagonal= new ArrayList<List<Coordinates>>();
        List<Coordinates> temp = new ArrayList<Coordinates>();
        for(int i=0;i<5;i++) temp.add(new Coordinates(i,i));
        diagonal.add(temp);
        temp = new ArrayList<Coordinates>();
        for(int i=0;i<5;i++) temp.add(new Coordinates(4-i,i));
        diagonal.add(temp);

        Shape test = new Shape(2,diagonal);
        assertTrue(test.check(passShelfDiagonal));
        assertFalse(test.check(dontPassShelfDiagonal));

        List<List<Coordinates>> xShape= new ArrayList<List<Coordinates>>();
        temp = new ArrayList<Coordinates>();
        for(int i=0;i<3;i++) temp.add(new Coordinates(i,i));
        temp.add(new Coordinates(0,2));
        temp.add(new Coordinates(2,0));
        diagonal.add(temp);

        test = new Shape(2,xShape);
        assertTrue(test.check(passShelfXShape));
        assertFalse(test.check(dontPassShelfXShape));
    }
}