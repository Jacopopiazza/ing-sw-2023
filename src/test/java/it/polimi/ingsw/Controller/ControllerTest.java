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
        assertFalse(controller.addPlayer("Picci", (message) -> {System.out.print("Picci");}));
        Assert.assertFalse(controller.isGameStarted());

        assertTrue(controller.addPlayer("Roma", (message) -> {System.out.print("Roma");}));
        assertTrue(controller.isGameStarted());
    }

    @Test
    public void testDoTurn_wrongPlayer() throws IllegalColumnInsertionException {
        AtomicBoolean cheatedFlag = new AtomicBoolean(false);
        controller.addPlayer("Picci", (message) -> {System.out.print("Picci");});
        controller.addPlayer("Roma", (message) -> {System.out.print("Roma");});
        controller.doTurn("Roma", new Coordinates[3], 0);
        assertTrue(cheatedFlag.get());
    }
}