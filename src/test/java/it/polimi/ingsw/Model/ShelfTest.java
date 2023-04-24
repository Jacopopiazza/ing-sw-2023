package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.NoTileException;
import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;

public class ShelfTest extends TestCase {
    private Shelf shelf;

    @Test
    public void addTile() {
    }

    @Test
    public void getTile() {
    }

    @Test
    public void getColumns() {
    }

    @Test
    public void getRows() {
    }

    @Test
    public void testIsFull() throws IllegalColumnInsertionException, NoTileException {
        int c = Shelf.getColumns();
        int r = Shelf.getRows();
        shelf = new Shelf();

        for(int i=0; i<c; i++){
            assertFalse(shelf.isFull());
            for(int j=0;j<r;j++){
                shelf.addTile(new Tile(TileColor.BLUE,0),i);
            }
        }
        assertTrue(shelf.isFull());
    }
}