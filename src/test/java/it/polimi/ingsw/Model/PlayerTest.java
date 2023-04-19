package it.polimi.ingsw.Model;


import it.polimi.ingsw.Exceptions.*;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest extends TestCase {

    Player p;
    PrivateGoal pg;
    int randomToken;

    @Before
    public void setUp(){
        pg = PrivateGoal.getPrivateGoals(2)[0];
        p = new Player(pg, "nickname");
        randomToken = 1000;
    }

    @Test
    public void testGettersAndSetScore() throws InvalidIndexException, NoTileException, IllegalColumnInsertionException {
        assertEquals(p.getNickname(),"nickname");
        assertEquals(p.getScore(),0);

        assertEquals(p.getPrivateGoal(),pg);
        pg = PrivateGoal.getPrivateGoals(2)[0];
        p.setGoal(pg);
        assertEquals(p.getPrivateGoal(),pg);

        p.setScore(10);
        assertEquals(p.getScore(),10);


        assertTrue(p.isActive());
        assertFalse(p.isWinner());

        p.setActive(false);
        assertFalse(p.isActive());

        p.setWinner(true);
        assertTrue(p.isWinner());

       for( int i = 0; i < p.getAccomplishedGlobalGoals().length; i++ ) {
           assertFalse( p.getAccomplishedGlobalGoals()[i] != 0 );
           p.setAccomplishedGlobalGoal(i, this.randomToken);
           assertTrue( p.getAccomplishedGlobalGoals()[i] == this.randomToken );
       }

        Shelf s = new Shelf();
        for(int i = 0; i < Shelf.getRows(); i++) {
            for(int j = 0; j< Shelf.getColumns(); j++) {
                assertEquals(p.getShelf().getTile(new Coordinates(i,j)), s.getTile(new Coordinates(i,j)) );
            }
        }

        s.addTile(new Tile(TileColor.BLUE, 0), 0);
        s.addTile(new Tile(TileColor.CYAN, 0), 0);
        s.addTile(new Tile(TileColor.FUCHSIA, 0), 0);
        s.addTile(new Tile(TileColor.YELLOW, 0), 0);
        s.addTile(new Tile(TileColor.GREEN, 0), 0);

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
        Assert.assertThrows(InvalidIndexException.class, () -> p.setAccomplishedGlobalGoal(-1, this.randomToken));
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
    public void testCheckPrivateGoal() throws MissingShelfException, IllegalColumnInsertionException, NoTileException {

        assertEquals(p.checkPrivateGoal(), false);

        boolean flag;
        Shelf shelfToTest = p.getShelf();
        Coordinates[] coord = pg.getCoordinates();

        // from bottom left, row by row, adds every tile
        for( int i = 0; i < Shelf.getRows()*Shelf.getColumns(); i++ ){
            flag = false;
            // for each Coordinate in the PrivateGoal, if present I add such tile (with color TileColor.values()[j])
            for( int j = 0; j < coord.length && !flag; j++ ) {
                if ( ( coord[j].getCOL() == i % Shelf.getColumns() ) && ( coord[j].getROW() == Shelf.getRows() - 1 - (i / Shelf.getRows()) ) ) {
                    shelfToTest.addTile(new Tile(TileColor.values()[j], 0), coord[j].getCOL());
                    flag = true;
                }
            }
            // otherwise adds a blue Tile (doesn't matter)
            if( !flag ) shelfToTest.addTile(new Tile(TileColor.BLUE, 0), i % Shelf.getColumns());
        }

        p.setShelf(shelfToTest);
        assertEquals(p.checkPrivateGoal(), true);
    }
}