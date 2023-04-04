package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.HashSet;

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
        Coordinates coord[] = PrivateGoal.getPrivateGoals(2)[0].getCoordinates();
        boolean flag;

        // from bottom left, row by row, adds every tile
        for( int i = 0; i < Shelf.getRows()*Shelf.getColumns(); i++ ){
            flag = false;
            // for each Coordinate in the PrivateGoal, if present I add such tile (with color TileColor.values()[j])
            for( int j = 0; j < coord.length && !flag; j++ ) {
                if ( ( coord[j].getY() == i % Shelf.getColumns() ) && ( coord[j].getX() == Shelf.getRows() - 1 - (i / Shelf.getRows()) ) ) {
                    shelfToTest.addTile(new Tile(TileColor.values()[j], 0), coord[j].getY());
                    flag = true;
                }
            }
            // otherwise adds a blue Tile (doesn't matter)
            if( !flag ) shelfToTest.addTile(new Tile(TileColor.BLUE, 0), i % Shelf.getColumns());
        }
        assertEquals(privateGoals[0].check(shelfToTest), 12);

    }
}