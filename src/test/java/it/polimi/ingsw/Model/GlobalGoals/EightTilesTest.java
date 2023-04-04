package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Exceptions.EmptyStackException;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.NoTileException;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.Tile;
import it.polimi.ingsw.Model.TileColor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class EightTilesTest {
    private EightTiles goal;
    private Shelf shelf;
    // Eight tiles of the same color
    @Before
    public void setUp(){
        shelf = new Shelf();
        goal = new EightTiles(2);
    }

    @Test
    public void popScore() throws EmptyStackException {
        Assert.assertEquals(goal.popScore(), 8);
    }

    @Test
    public void check() throws IllegalColumnInsertionException, NoTileException {
        shelf.addTile(new Tile(TileColor.BLUE, 0), 0);
        shelf.addTile(new Tile(TileColor.BLUE, 0), 0);
        shelf.addTile(new Tile(TileColor.BLUE, 0), 0);
        shelf.addTile(new Tile(TileColor.BLUE, 0), 0);
        shelf.addTile(new Tile(TileColor.BLUE, 0), 0);
        shelf.addTile(new Tile(TileColor.BLUE, 0), 0);
        shelf.addTile(new Tile(TileColor.YELLOW, 0), 1);
        shelf.addTile(new Tile(TileColor.BLUE, 0), 1);
        Assert.assertFalse(goal.check(shelf));
        shelf.addTile(new Tile(TileColor.BLUE, 0), 1);
        Assert.assertTrue(goal.check(shelf));

        shelf.addTile(new Tile(TileColor.GREEN, 0), 1);
        shelf.addTile(new Tile(TileColor.GREEN, 0), 1);
        shelf.addTile(new Tile(TileColor.GREEN, 0), 1);
        shelf.addTile(new Tile(TileColor.GREEN, 0), 2);
        shelf.addTile(new Tile(TileColor.GREEN, 0), 2);
        shelf.addTile(new Tile(TileColor.GREEN, 0), 2);
        shelf.addTile(new Tile(TileColor.GREEN, 0), 2);
        Assert.assertTrue(goal.check(shelf));
        shelf.addTile(new Tile(TileColor.GREEN, 0), 2);
        Assert.assertTrue(goal.check(shelf));
    }
}