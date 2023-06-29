package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Messages.UpdateViewMessage;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.ModelView.GameView;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class ControllerTest extends TestCase {
    private Controller controller;
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
    public void testGetNumOfActivePlayers() {
        assertEquals(0, controller.getNumOfActivePlayers());
        controller.addPlayer("Picci", (message) -> {System.out.print("Picci");});
        assertEquals(1, controller.getNumOfActivePlayers());
        controller.addPlayer("Roma", (message) -> {System.out.print("Roma");});
        assertEquals(2, controller.getNumOfActivePlayers());
        controller.disconnect("Picci");
        assertEquals(1, controller.getNumOfActivePlayers());
    }

    @Test
    public void testReconnect() {
        assertEquals(0, controller.getNumOfActivePlayers());
        controller.addPlayer("Picci", (message) -> {System.out.print("Picci");});
        assertEquals(1, controller.getNumOfActivePlayers());
        controller.addPlayer("Roma", (message) -> {System.out.print("Roma");});
        assertEquals(2, controller.getNumOfActivePlayers());
        controller.disconnect("Picci");
        assertEquals(1, controller.getNumOfActivePlayers());
        controller.reconnect("Picci", (message) -> {System.out.print("New Picci");});
        assertEquals(2, controller.getNumOfActivePlayers());
    }

    @Test
    public void testGetListener(){
        controller.addPlayer("Picci", (message) -> {System.out.print("Picci");});
        controller.addPlayer("Roma", (message) -> {System.out.print("Roma");});
        try{
            assertNotNull(controller.getListener("Picci"));
        }catch (Exception e){
            fail();
        }

    }

    @Test
    public void testDoTurn_wrongPlayer() throws IllegalColumnInsertionException, InterruptedException {

          class BooleanForTest{
            private boolean value;
            public BooleanForTest(boolean value){
                synchronized (this){
                    this.value = value;
                }
            }
            public boolean getValue(){
                synchronized (this){
                    return value;
                }

            }
            public void setValue(boolean value){
                synchronized (this){
                    this.value = value;
                }
            }
        }
        Object obj = new Object();
        BooleanForTest bool = new BooleanForTest(false);
        controller.addPlayer("Picci", (message) -> {

            if(message instanceof UpdateViewMessage){
                UpdateViewMessage m = (UpdateViewMessage) message;
                if(m.getGameView().getCheater() !=null && m.getGameView().getCheater().equals("Picci")){
                    bool.setValue(true);
                    synchronized (obj) {
                        obj.notify();
                    }
                }

            }


        });
        controller.addPlayer("Roma", (message) -> {
            if(message instanceof UpdateViewMessage){
                UpdateViewMessage m = (UpdateViewMessage) message;
                if(m.getGameView().getCheater() !=null && m.getGameView().getCheater().equals("Roma")){
                    bool.setValue(true);
                    synchronized (obj) {
                        obj.notify();
                    }
                }

            }

        });
        controller.doTurn("Roma", new Coordinates[3], 0);
        Thread.sleep(1000);
        assertTrue(bool.getValue());
    }
}