package it.polimi.ingsw.Model;

import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class TileTest extends TestCase {

    Tile tile;

    @Before
    public void setUp() throws Exception {
        tile = new Tile(TileColor.BLUE, 0);
    }

    @Test
    public void getColor() {
        assertEquals(tile.getColor(), TileColor.BLUE);
    }

    @Test
    public void getId() {
        assertEquals(tile.getId(), 0);
    }
}