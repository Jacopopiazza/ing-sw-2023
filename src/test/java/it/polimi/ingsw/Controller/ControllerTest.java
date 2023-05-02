package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.NotYourTurnException;
import it.polimi.ingsw.Model.Coordinates;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.EventListener;

public class ControllerTest extends TestCase {
    private Controller controller;
    private Server server;

    Coordinates wrongNumberOfTilesActionCoords[];
    int wrongNumberOfTilesActionColumn = 1;

    Coordinates wrongRowsAndColumnsActionCoords[];

    Coordinates wrongColumnActionCoords[];
    int wrongColumnActionColumn = -1;


    @Before
    public void setUp() throws Exception {
        controller = new Controller(2, server);

        Coordinates wrongNumberOfTilesActionCoords[] = { new Coordinates(1,3), new Coordinates(1, 4), null, null };
        int wrongNumberOfTilesActionColumn = 1;

        Coordinates rightActionCoords[] = { new Coordinates(1,3), new Coordinates(1, 4) };

        Coordinates wrongRowsAndColumnsActionCoords[] = { new Coordinates(1,3), new Coordinates(1, 4), new Coordinates(2,3) };

        int wrongColumnActionColumn = -1;

    }

    @Test
    public void testIsGameStarted() {
        Assert.assertFalse(controller.isGameStarted());
    }

    @Test
    public void testAddPlayer() {
        EventListener listener_Picci = new EventListener() {
            @Override
            public String toString() {
                return super.toString();
            }
        };
        EventListener listener_Roma = new EventListener() {
            @Override
            public String toString() {
                return super.toString();
            }
        };

        assertFalse(controller.addPlayer("Picci", listener_Picci));
        Assert.assertFalse(controller.isGameStarted());

        assertTrue(controller.addPlayer("Roma", listener_Roma));
        assertTrue(controller.isGameStarted());
    }

    @Test
    public void test_NotYourTurnException() throws NotYourTurnException, IllegalColumnInsertionException {
        controller.addPlayer("Picci", null);
        Assert.assertThrows(NotYourTurnException.class, () -> controller.doTurn("Roma", null, 0));
    }

    @Test
    public void test_IllegalColumnInsertionException() throws IllegalColumnInsertionException, NotYourTurnException {
        controller.addPlayer("Picci", null);
        Assert.assertThrows(IllegalColumnInsertionException.class, () -> controller.doTurn("Picci", wrongColumnActionCoords, wrongColumnActionColumn));
    }
}