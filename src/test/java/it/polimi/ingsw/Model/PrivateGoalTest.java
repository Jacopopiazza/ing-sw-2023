package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.ColumnOutOfBoundsException;
import it.polimi.ingsw.Exceptions.InvalidNumberOfPlayersException;
import it.polimi.ingsw.Exceptions.MissingShelfException;
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
    public void testCheckMaximumPoints() throws InvalidNumberOfPlayersException, MissingShelfException {
        Shelf shelfToTest = new Shelf();
        Coordinates coord[] = PrivateGoal.getPrivateGoals(2)[0].getCoordinates();

        for(int i = 0; i < coord.length; i++){
            shelfToTest.setTile(coord[i], TileColor.values()[i]);
        }

        assertEquals(privateGoals[0].check(shelfToTest), 12);

    }
}