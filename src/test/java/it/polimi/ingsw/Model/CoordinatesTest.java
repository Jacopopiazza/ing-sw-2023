package it.polimi.ingsw.Model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CoordinatesTest {

    private Coordinates coordinates;

    @Before
    public void setUp() throws Exception {
        coordinates = new Coordinates(0, 0);
    }

    @Test
    public void testGetX() {
        assertEquals(coordinates.getROW(), 0);
    }

    @Test
    public void testGetY() {
        assertEquals(coordinates.getCOL(), 0);
    }

}