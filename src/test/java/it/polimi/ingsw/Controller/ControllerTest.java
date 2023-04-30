package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.InvalidIndexException;
import it.polimi.ingsw.Exceptions.NotValidChosenTiles;
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

    private Action rightAction;
    private Action wrongNumberOfTilesAction;
    private Action wrongRowsAndColumnsAction;

    private Action wrongColumnInsertion;

    @Before
    public void setUp() throws Exception {
        controller = new Controller(2, server);

        rightAction = new Action(){
            private Coordinates[] rightCoords = new Coordinates[2];

            public Coordinates[] getChosenTiles(){
                rightCoords[0] = new Coordinates(1,3);
                rightCoords[1] = new Coordinates(1, 4);

                return rightCoords;
            }

            public int getColumn(){
                return 1;
            }
        };

        wrongNumberOfTilesAction = new Action() {
            private Coordinates[] wrongCoords = new Coordinates[4];

            public Coordinates[] getChosenTiles(){
                wrongCoords[0] = new Coordinates(1,3);
                wrongCoords[1] = new Coordinates(1, 4);
                wrongCoords[2] = null;
                wrongCoords[3] = null;

                return wrongCoords;
            }
        };

        wrongRowsAndColumnsAction = new Action(){
            private Coordinates[] wrongCoords = new Coordinates[3];

            public Coordinates[] getChosenTiles(){
                wrongCoords[0] = new Coordinates(1,3);
                wrongCoords[1] = new Coordinates(1, 4);
                wrongCoords[2] = new Coordinates(2,3);
                return wrongCoords;
            }
        };

        wrongColumnInsertion = new Action(){
            public int getColumn(){
                return -1;
            }
        };

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
    public void testPerform() throws NotYourTurnException, NotValidChosenTiles, IllegalColumnInsertionException {
        controller.addPlayer("Picci", null);
        controller.addPlayer("Roma", null);

        controller.perform("Picci", rightAction);
    }

    @Test
    public void test_NotYourTurnException() throws NotYourTurnException, NotValidChosenTiles, IllegalColumnInsertionException {
        controller.addPlayer("Picci", null);
        Assert.assertThrows(NotYourTurnException.class, () -> controller.perform("Roma", null));
    }

    // Action implementation needed
    @Test (expected = NotValidChosenTiles.class)
    public void test_NotValidChosenTilesException() throws NotValidChosenTiles, IllegalColumnInsertionException, NotYourTurnException {
        controller.addPlayer("Picci", null);
        Assert.assertThrows(NotValidChosenTiles.class, () -> controller.perform("Picci", wrongNumberOfTilesAction));
        Assert.assertThrows(NotValidChosenTiles.class, () -> controller.perform("Picci", wrongRowsAndColumnsAction));
    }


    @Test (expected = IllegalColumnInsertionException.class)
    public void test_IllegalColumnInsertionException() throws NotValidChosenTiles, IllegalColumnInsertionException, NotYourTurnException {
        controller.addPlayer("Picci", null);
        Assert.assertThrows(IllegalColumnInsertionException.class, () -> controller.perform("Picci", wrongColumnInsertion));
    }
}