package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest extends TestCase {

    Player p;
    PrivateGoal pg;

    @Before
    public void setUp(){
        pg = PrivateGoal.getPrivateGoals(2)[0];
        p = new Player(pg, "nickname");
    }

    @Test
    public void testGettersAndSetScore() throws InvalidIndexException {
        assertEquals(p.getNickname(),"nickname");
        assertEquals(p.getScore(),0);

        assertEquals(p.getGoal(),pg);
        pg = PrivateGoal.getPrivateGoals(2)[0];
        p.setGoal(pg);
        assertEquals(p.getGoal(),pg);

        p.setScore(10);
        assertEquals(p.getScore(),10);
        assertEquals(p.getPrivateGoal(),pg);

        assertTrue(p.isActive());
        assertFalse(p.isWinner());

        p.setActive(false);
        assertFalse(p.isActive());

        p.setWinner(true);
        assertTrue(p.isWinner());

       for(int i = 0;i < p.getAccomplishedGlobalGoals().length; i++) {
           assertFalse(p.getAccomplishedGlobalGoals()[i]);
           p.setAccomplishedGlobalGoal(i);
           assertTrue(p.getAccomplishedGlobalGoals()[i]);
       }

        Shelf s = new Shelf();
        for(int i = 0; i < Shelf.getRows(); i++) {
            for(int j = 0; j< Shelf.getColumns(); j++) {
                assertEquals(p.getShelf().getTile(new Coordinates(i,j)), s.getTile(new Coordinates(i,j)) );
            }
        }

        s.setTile(new Coordinates(0,0) , TileColor.BLUE);
        s.setTile(new Coordinates(1,0) , TileColor.CYAN);
        s.setTile(new Coordinates(2,0) , TileColor.FUCHSIA);
        s.setTile(new Coordinates(3,0) , TileColor.YELLOW);
        s.setTile(new Coordinates(4,0) , TileColor.GREEN);

        p.setShelf(s);
        for(int i = 0; i < Shelf.getRows(); i++) {
            for(int j = 0; j< Shelf.getColumns(); j++) {
                assertEquals(p.getShelf().getTile(new Coordinates(i,j)), s.getTile(new Coordinates(i,j)) );
            }
        }
    }

    @Test (expected = InvalidIndexException.class)
    public void setAccomplishedGoals() throws InvalidIndexException{
        p.setAccomplishedGlobalGoal(-1);
    }

    @Test (expected = NonValidScoreException.class)
    public void setScoreNegativeScore() {
        p.setScore(-1);
    }

    @Test (expected = MissingShelfException.class)
    public void testMissingShelfInSetShelf(){
        p.setShelf(null);
    }

    @Test (expected = NoTileException.class)
    public void insertNullTiles() throws NoTileException, IllegalColumnInsertionException {
        p.insert(null,0);
    }

    @Test (expected = IllegalColumnInsertionException.class)
    public void insertInvalidColumn() throws NoTileException, IllegalColumnInsertionException {
        Tile[] tiles = new Tile[]{};
        p.insert(tiles,-1);
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