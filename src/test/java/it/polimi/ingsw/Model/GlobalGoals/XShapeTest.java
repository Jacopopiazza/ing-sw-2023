package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Exceptions.EmptyStackException;
import it.polimi.ingsw.Exceptions.InvalidNumberOfPlayersException;
import it.polimi.ingsw.Exceptions.MissingShelfException;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.Model.TileSack;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class XShapeTest extends TestCase {

    private Shelf shelf;
    private XShape goal;

    @Before
    public void setUp() throws MissingShelfException, InvalidNumberOfPlayersException {
        shelf = new Shelf();
        shelf.setTile(new Coordinates(2,2), TileColor.BLUE);
        shelf.setTile(new Coordinates(2, 4), TileColor.BLUE);
        shelf.setTile(new Coordinates(4,2), TileColor.BLUE);
        shelf.setTile(new Coordinates(4, 4), TileColor.BLUE);
        shelf.setTile(new Coordinates(3, 3), TileColor.BLUE);

        goal = new XShape(2);
    }

    @Test
    public void popScore() throws EmptyStackException {
        assertEquals(goal.popScore(), 8);
    }

    @Test
    public void testCheck() throws MissingShelfException {
        assertTrue(goal.check(shelf));
    }
}