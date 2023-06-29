package it.polimi.ingsw.Model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Utilities.Config;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

import static org.junit.Assert.*;

public class PrivateGoalTest {

    PrivateGoal[] privateGoals;

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

    @Test
    public void testCheck_CorrectlyThrowsInvalidNumberOfPlayersException() throws InvalidNumberOfPlayersException {
        assertThrows(InvalidNumberOfPlayersException.class, () -> PrivateGoal.getPrivateGoals(-1));
    }

    @Test
    public void testNoDuplicatesInJSON() {
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(PrivateGoal.class.getResourceAsStream("/PrivateGoals.json"));

        JsonArray baseArray = gson.fromJson(reader, JsonArray.class);
        List<Coordinates[]> allPrivateGoals = new ArrayList<Coordinates[]>();

        for (JsonElement jsonPrivateGaol : baseArray) {
            JsonArray prvGoal = jsonPrivateGaol.getAsJsonArray();
            Coordinates[] coords = gson.fromJson(prvGoal, Coordinates[].class);
            allPrivateGoals.add(coords);
        }

        Set<Coordinates[]> set = new HashSet<>();
        for(int i = 0;i<allPrivateGoals.size();i++){
            assertFalse(set.contains(allPrivateGoals.get(i)));
            set.add(allPrivateGoals.get(i));
            Set<Coordinates> internalSet = new HashSet<>();
            for(int j = 0;j<allPrivateGoals.get(i).length;j++){
                assertFalse(internalSet.contains(allPrivateGoals.get(i)[j]));
                internalSet.add(allPrivateGoals.get(i)[j]);
            }
        }
    }

    @Test
    public void testCheckMaximumPoints() throws InvalidNumberOfPlayersException, MissingShelfException, IllegalColumnInsertionException, NoTileException {

        // Take all private goals with surprisingly dumb approach
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(PrivateGoal.class.getResourceAsStream("/PrivateGoals.json"));
        JsonArray baseArray = gson.fromJson(reader, JsonArray.class);
        Set<PrivateGoal> allPrivateGoals = new HashSet<>();
        PrivateGoal prGoal;
        while( allPrivateGoals.size() < baseArray.size() ){
            prGoal = PrivateGoal.getPrivateGoals(2)[0];
            if( allPrivateGoals.contains(prGoal) ) continue;
            allPrivateGoals.add(prGoal);
        }

        // For each existing privateGoal
        for ( int index=0; index < allPrivateGoals.size(); index++ ) {
            // Get the array of Coordinates of the current PrivateGoal, through an apparently useless conversion
            Coordinates[] unsortedCoord = allPrivateGoals.toArray(PrivateGoal[]::new)[index].getCoordinates();
            Shelf shelfToTest = new Shelf();
            Map<Coordinates,TileColor> map = new HashMap<>();

            // Map to every Coordinate the right color, according to a complex logic to manage it that I do not agree with but my opinion was never asked about such implementation, thanks guys
            for( int i=0; i<unsortedCoord.length; i++ ){
                map.put(unsortedCoord[i], TileColor.values()[i]);
            }

            // Sort Coordinates by lowest column and highest row (this whole thing was completely unnecessary)
            Coordinates[] coord = Arrays.stream(unsortedCoord).sorted((c1,c2) -> {
                if( c1.equals(c2) ) return 0;
                if( ( c1.getCOL() < c2.getCOL() ) || ( ( c1.getCOL() == c2.getCOL() ) && ( c1.getROW() > c2.getROW() ) ) ) return -1;
                return 1;
            }).toArray(Coordinates[]::new);

            // Initialized because the compiler plays the enemy role at times
            int lastRow = -1;
            // Initialized lastColumn to a value which is not in the domain of valid columns
            int lastColumn = -1;

            // Way too complex approach just to fill the shelfToTest
            for( int num = 0; num < coord.length; num++ ){
                Coordinates c = coord[num];
                int row = c.getROW();
                int column = c.getCOL();

                if( lastColumn != column ) {
                    lastColumn = column;
                    lastRow = Config.getInstance().getShelfRows();
                }

                // Add random tiles with random (completely unnecessary) index until row is reached, moving from the bottom to the top of the shelfToTest
                for( int i = lastRow-1; i > row; i-- )
                    shelfToTest.addTile(new Tile(TileColor.BLUE, new Random(120).nextInt()),column);

                // Add Tile from the map to the shelfToTest (why not using 0 as index? ok)
                shelfToTest.addTile(new Tile(map.get(c), new Random(120).nextInt()), column);

                // Now it's time to save the lastRow
                lastRow = row;
            }

            // Check if the score is right for the current PrivateGoal
            int res = allPrivateGoals.toArray(PrivateGoal[]::new)[index].check(shelfToTest);
            assertEquals(res, 12);
        }
    }
}