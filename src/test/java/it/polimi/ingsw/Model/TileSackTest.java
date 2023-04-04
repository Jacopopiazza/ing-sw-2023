package it.polimi.ingsw.Model;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
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
        int tmp = 0;
        int[] remaining = sack.getRemaining();

        Tile tile = sack.pop();

        assertNotNull(tile);
        assertNotNull(tile.getColor());
        assertTrue(Arrays.stream(TileColor.values()).collect(Collectors.toList()).contains(tile.getColor()));

        for(int i = 0; i < remaining.length; i++){
            tmp += remaining[i];
        }
        assertEquals(sackSize - 1, tmp);
    }

}