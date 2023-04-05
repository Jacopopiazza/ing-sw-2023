package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.*;

import static org.junit.Assert.*;

public class PrivateGoalTest {

    PrivateGoal privateGoals[];

    @Before
    public void setUp() throws FileNotFoundException {
    }

    public static boolean hasDuplicates(Coordinates[] arr) {
        HashSet<Coordinates> set = new HashSet<Coordinates>();
        for (int i = 0; i < arr.length; i++) {
            if (set.contains(arr[i])) {
                return true; // found a duplicate
            }
            set.add(arr[i]);
        }
        return false; // no duplicates found
    }

    @Test
    public void testGetNoDuplicatePrivateGoals() throws InvalidNumberOfPlayersException {
        for(int people = 2; people < 4; people++) {
            privateGoals = PrivateGoal.getPrivateGoals(people);
            assertNotNull(privateGoals);
            assertEquals(privateGoals.length, people);
            for (int i = 0; i < people; i++) {
                assertNotNull(privateGoals[i]);
                assertEquals(privateGoals[i].getCoordinates().length, TileColor.values().length);
                // checks there are no duplicate
                assertFalse(hasDuplicates(privateGoals[i].getCoordinates()));
            }
        }
    }

    @Test (expected = InvalidNumberOfPlayersException.class)
    public void testCheck_CorrectlyThrowsInvalidNumberOfPlayersException() throws InvalidNumberOfPlayersException {
        privateGoals = PrivateGoal.getPrivateGoals(-1);
    }



    @Test
    public void testCheckMaximumPoints() throws InvalidNumberOfPlayersException, MissingShelfException, IllegalColumnInsertionException, NoTileException {
        Shelf shelfToTest = new Shelf();
        privateGoals = PrivateGoal.getPrivateGoals(2);
        Coordinates[] coord = privateGoals[0].getCoordinates();
        Map<Coordinates,TileColor> map = new HashMap<>();
        for(int i = 0;i<coord.length;i++){
            map.put(coord[i], TileColor.values()[i]);
        }

        coord = Arrays.stream(coord).sorted((c1,c2) -> {
            int val = 1;
            if (c1.equals(c2)) return 0;
            if(c1.getY() < c2.getY() || c1.getY() == c2.getY() && c1.getX() < c2.getX()) return -1;

            return val;

        }).toArray(Coordinates[]::new);

        int lastRow = -1;
        int lastColumn = -1;

        for(int numero = 0; numero < coord.length; numero++){
            Coordinates c = coord[numero];
            int row = c.getX();
            int column = c.getY();

            if(lastColumn != column) {
                lastColumn = column;
                lastRow = 0;
            }
            else{
                lastRow += 1;
            }
            //Add casual tiles of casual color to reach the right row
            for(int i = lastRow; i < row;i++){
                shelfToTest.addTile(new Tile(TileColor.BLUE, new Random(120).nextInt()),column);
            }
            //Add current tile of the correct color in the correct row
            shelfToTest.addTile(new Tile(map.get(c), new Random(120).nextInt()), column);
            lastRow = row;



        }

        assertEquals(privateGoals[0].check(shelfToTest), 12);

    }
}