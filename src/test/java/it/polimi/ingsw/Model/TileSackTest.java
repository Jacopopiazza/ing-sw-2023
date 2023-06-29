package it.polimi.ingsw.Model;

import it.polimi.ingsw.Utilities.Config;
import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

public class TileSackTest extends TestCase {

    private TileSack sack;
    private int sackSize;

    @Before
    public void setUp() {
        sack = new TileSack();
        int[] remaining = sack.getRemaining();
        sackSize = 0;
        for(int i = 0; i < remaining.length; i++){
            sackSize += remaining[i];
        }
    }

    @Test
    public void testPop() {
        int[] remaining = sack.getRemaining();
        int tot = Arrays.stream(remaining).sum();
        Map<TileColor, List<Tile>> popped = new HashMap<TileColor, List<Tile>>();
        for(TileColor c : TileColor.values()){
            popped.put(c, new LinkedList<>());
        }

        for(int i=0; i<tot; i++){
            Tile tile = sack.pop();
            popped.get(tile.getColor()).add(tile);

            assertNotNull(tile);
            assertNotNull(tile.getColor());
            assertTrue(Arrays.stream(TileColor.values()).collect(Collectors.toList()).contains(tile.getColor()));
            assertEquals(tile.getId(),popped.get(tile.getColor()).size()-1);
            assertEquals(sackSize - 1 - i, Arrays.stream(sack.getRemaining()).sum());
        }


        for(TileColor c : TileColor.values()){
            assertEquals(Config.getInstance().getNumOfTilesPerColor(),popped.get(c).size());
        }

        assertEquals(null,sack.pop());

    }

}