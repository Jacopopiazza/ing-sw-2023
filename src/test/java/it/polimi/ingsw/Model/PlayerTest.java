package it.polimi.ingsw.Model;

import com.sun.jdi.event.ExceptionEvent;
import it.polimi.ingsw.Exceptions.InvalidIndexException;
import it.polimi.ingsw.Exceptions.MissingShelfException;
import it.polimi.ingsw.Exceptions.NonValidScoreException;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class PlayerTest extends TestCase {

    Player p;
    PrivateGoal pg;

    @Before
    public void setUp(){
        pg = PrivateGoal.getPrivateGoals(1)[0];
        p = new Player(pg, "nickname");
    }

    @Test
    public void testGettersAndSetScore() {
        assertEquals(p.getNickname(),"nickname");
        assertEquals(p.getScore(),0);
        p.setScore(10);
        assertEquals(p.getScore(),10);
        assertEquals(p.getPrivateGoal(),pg);

        assertTrue(p.isActive());
        assertFalse(p.isWinner());

        p.setActive(false);
        assertFalse(p.isActive());

        p.setWinner(true);
        assertTrue(p.isWinner());

        Shelf s = new Shelf();
        assertEquals(p.getShelf(), s);

        s.setTile(new Coordinates(0,0) , TileColor.BLUE);
        s.setTile(new Coordinates(1,0) , TileColor.CYAN);
        s.setTile(new Coordinates(2,0) , TileColor.FUCHSIA);
        s.setTile(new Coordinates(3,0) , TileColor.YELLOW);
        s.setTile(new Coordinates(4,0) , TileColor.GREEN);

        p.setShelf(s);
        assertEquals(p.getShelf(),s);
    }

    @Test (expected = NonValidScoreException.class)
    public void setScoreNegativeScore() {
        p.setScore(-1);
    }

    @Test (expected = MissingShelfException.class)
    public void testMissingShelfInSetShelf(){
        p.setShelf(null);
    }

    @Test
    public void insert() {

    }

    @Test
    public void getAccomplishedGlobalGoals() {
    }

    @Test (expected = InvalidIndexException.class)
    public void setAccomplishedGlobalGoal() {
    }

    @Test
    public void checkPrivateGoal() throws MissingShelfException {

        assertEquals(p.checkPrivateGoal(), false);

        Shelf shelf = p.getShelf();
        Coordinates[] coords = pg.getCoordinates();

        for(int i = 0;i< coords.length; i++){
            Coordinates c = coords[i];
            TileColor color = TileColor.values()[i];
            shelf.setTile(c,color);
        }

        p.setShelf(shelf);
        assertEquals(p.checkPrivateGoal(), true);
    }
}