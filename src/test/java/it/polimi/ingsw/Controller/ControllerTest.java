package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Game;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerTest extends TestCase {
    private Controller controller;

    Coordinates[] wrongNumberOfTilesActionCoords;
    int wrongNumberOfTilesActionColumn = 1;

    Coordinates[] wrongRowsAndColumnsActionCoords;

    Coordinates[] wrongColumnActionCoords;
    final int wrongColumnActionColumn = -1;


    @Before
    public void setUp() {
        controller = new Controller(new Game(2), null);
    }

    @Test
    public void testIsGameStarted() {
        Assert.assertFalse(controller.isGameStarted());
    }

    @Test
    public void testAddPlayer() {
        assertFalse(controller.addPlayer("Picci", null));
        Assert.assertFalse(controller.isGameStarted());

        assertTrue(controller.addPlayer("Roma", null));
        assertTrue(controller.isGameStarted());
    }

    @Test
    public void testDoTurn_wrongPlayer() throws IllegalColumnInsertionException {
        AtomicBoolean cheatedFlag = new AtomicBoolean(false);
        controller.addPlayer("Picci", null);
        controller.addPlayer("Roma", (message) -> { cheatedFlag.set(true);});
        controller.doTurn("Roma", null, 0);
        assertTrue(cheatedFlag.get());
    }

    @Test
    public void test_IllegalColumnInsertionException() throws IllegalColumnInsertionException {
        controller.addPlayer("Picci", null);
        Assert.assertThrows(IllegalColumnInsertionException.class, () -> controller.doTurn("Picci", wrongColumnActionCoords, wrongColumnActionColumn));
    }
}