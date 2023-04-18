package it.polimi.ingsw.Model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
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

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(PrivateGoal.class.getResourceAsStream("/PrivateGoals.json"));
        JsonArray baseArray = gson.fromJson(reader, JsonArray.class);

        Set<PrivateGoal> allPrivateGoals = new HashSet<>();
        Set<Coordinates[]> allCoords = new HashSet<>();
        while (allPrivateGoals.size() < baseArray.size()){
            PrivateGoal prGoal = PrivateGoal.getPrivateGoals(2)[0];
            if(allPrivateGoals.contains(prGoal)) continue;
            allPrivateGoals.add(prGoal);
            allCoords.add(prGoal.getCoordinates());
        }


        for ( int index = 0;index<allPrivateGoals.size();index++) {

            Coordinates[] unsortedCoord = allPrivateGoals.toArray(PrivateGoal[]::new)[index].getCoordinates();
            Shelf shelfToTest = new Shelf();
            Map<Coordinates,TileColor> map = new HashMap<>();
            for(int i = 0;i<unsortedCoord.length;i++){
                map.put(unsortedCoord[i], TileColor.values()[i]);
            }

            Coordinates[] coord = Arrays.stream(unsortedCoord).sorted((c1,c2) -> {
                int val = 1;
                if (c1.equals(c2)) return 0;
                if(c1.getY() < c2.getY() || c1.getY() == c2.getY() && c1.getX() > c2.getX()) return -1;

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
                    lastRow = Config.getInstance().getShelfRows();
                }

                //Add casual tiles of casual color to reach the right row
                for(int i = lastRow-1; i > row;i--){
                    shelfToTest.addTile(new Tile(TileColor.BLUE, new Random(120).nextInt()),column);
                }
                //Add current tile of the correct color in the correct row
                shelfToTest.addTile(new Tile(map.get(c), new Random(120).nextInt()), column);
                lastRow = row;

            }


            int res = allPrivateGoals.toArray(PrivateGoal[]::new)[index].check(shelfToTest);
            assertEquals(res, 12);
        }
    }
}