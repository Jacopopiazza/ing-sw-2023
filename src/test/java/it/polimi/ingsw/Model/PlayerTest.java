package it.polimi.ingsw.Model;


import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;
import junit.framework.TestCase;
import junit.framework.TestResult;
import org.junit.Assert;
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
    public void testGettersAndSetScore() throws InvalidIndexException, NoTileException, IllegalColumnInsertionException {
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

        p.setShelf(new Shelf());
        Tile t = new Tile(TileColor.BLUE,123);
        p.insert(new Tile[]{t},0);

        assertEquals(p.getShelf().getTile(new Coordinates(Shelf.getRows()-1,0)), t);


    }

    @Test
    public void testSetAccomplishedGoals() throws InvalidIndexException{
        Assert.assertThrows(InvalidIndexException.class, () -> p.setAccomplishedGlobalGoal(-1));
    }

    @Test
    public void testSetScoreNegativeScore() throws NonValidScoreException {
        Assert.assertThrows(NonValidScoreException.class, () -> p.setScore(-1));
    }

    @Test
    public void testMissingShelfInSetShelf() {
        Assert.assertThrows(MissingShelfException.class, () -> p.setShelf(null));
    }

    @Test
    public void testInsertNullTiles() throws NoTileException, IllegalColumnInsertionException {
        Assert.assertThrows(NoTileException.class, () -> p.insert(null,0));
    }

    @Test
    public void testInsertOutOfBoundsColumn() throws NoTileException, IllegalColumnInsertionException, ColumnOutOfBoundsException {
        Tile[] tiles = new Tile[]{ new Tile(TileColor.BLUE, 123)};
        Assert.assertThrows(ColumnOutOfBoundsException.class, () -> p.insert(tiles,-1));
    }

    @Test
    public void testInsertInFullColumn() throws NoTileException, IllegalColumnInsertionException, ColumnOutOfBoundsException {
        Tile[] tiles = new Tile[]{ new Tile(TileColor.BLUE, 123),
                new Tile(TileColor.YELLOW, 234),
                new Tile(TileColor.CYAN, 345),
                new Tile(TileColor.FUCHSIA, 456),
                new Tile(TileColor.GREEN, 567),
                new Tile(TileColor.WHITE, 789),
                new Tile(TileColor.WHITE, 899)
        };
        Assert.assertThrows(IllegalColumnInsertionException.class, () -> p.insert(tiles,0));
    }

    @Test
    public void testFirstPlayer(){
        int score = p.getScore();
        p.first();
        assertEquals(score+1,p.getScore());
    }

    @Test
    public void testCheckPrivateGoal() throws MissingShelfException {

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