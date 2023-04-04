package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.*;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

public class XShapeTest extends TestCase {

    private Shelf shelf;
    private XShape goal;

    @Before
    public void setUp(){
        shelf = new Shelf();
        goal = new XShape(2);
    }

    @Test
    public void popScore() throws EmptyStackException {
        assertEquals(goal.popScore(), 8);
    }

    @Test
    public void testCheck() throws MissingShelfException, IllegalColumnInsertionException, NoTileException {
        for( int i = 0; i < 9; i++ ){
            if( i % 3 == 2 ) shelf.addTile(new Tile(TileColor.BLUE, 0), i%3 );
            else shelf.addTile(new Tile(TileColor.GREEN, 0),  i % 3);
        }
        assertFalse(goal.check(shelf));

        for( int i = 0; i < 6; i++ ) shelf.addTile(new Tile(TileColor.BLUE, 0), 3 + i % 2 );
        assertTrue(goal.check(shelf));
    }

    @Test (expected = MissingShelfException.class)
    public void testCheck_CorrectlyThrowsMissingShelfException() throws MissingShelfException, IllegalColumnInsertionException, NoTileException {
        Assert.assertThrows(MissingShelfException.class, () -> goal.check(null));
    }
}