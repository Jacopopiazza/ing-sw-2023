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
    int exampleShelf[][] = new int[5][6];   // generate a shelf of int prebuilt


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
    public void testGetRightPrivateGoals() throws InvalidNumberOfPlayersException {
        for(int people = 2; people < 4; people++) {
            privateGoals = PrivateGoal.privateGoalsForNPeople(people);
            assertEquals(privateGoals.length, people);
            for (int i = 0; i < people; i++) {
                assertEquals(privateGoals[i].getCoordinates().length, TileColor.values().length);
                // checks there are no duplicate
                assertFalse(hasDuplicates(privateGoals[i].getCoordinates()));
            }
        }
    }

    @Test
    public void check() throws MissingShelfException, ColumnOutOfBoundsException, InvalidNumberOfPlayersException {
    }
}