package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.EmptySackException;
import it.polimi.ingsw.Exceptions.InvalidCoordinatesForCurrentGameException;
import it.polimi.ingsw.Exceptions.InvalidIndexException;
import it.polimi.ingsw.Exceptions.UsernameNotFoundException;
import it.polimi.ingsw.Messages.Message;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;

public class GameTest extends TestCase {
    Game game;
    Game gameToRefill;
    Game gameToRefillButNoTiles;
    Game gameToRefillButNotEnoughTiles;

    @Before
    public void setUp() throws FileNotFoundException {
        game = new Game(2);
        game.addPlayer("Picci",(Message message)->{});
        game.addPlayer("Roma",(Message message)->{});
        game.init();

        gameToRefill = new Game(2);
        gameToRefill.addPlayer("Picci",(Message message)->{});
        gameToRefill.addPlayer("Roma",(Message message)->{});
        gameToRefill.init();
        for( Coordinates c : gameToRefill.getGameBoard().getCoords() ){
            try{
                gameToRefill.getGameBoard().setTile(c, null);
            }
            catch( InvalidCoordinatesForCurrentGameException e ){}
        }

        gameToRefillButNoTiles = new Game(2);
        gameToRefillButNoTiles.addPlayer("Picci",(Message message)->{});
        gameToRefillButNoTiles.addPlayer("Roma",(Message message)->{});
        gameToRefillButNoTiles.init();
        for( Coordinates c : gameToRefillButNoTiles.getGameBoard().getCoords() ){
            try{
                gameToRefillButNoTiles.getGameBoard().setTile(c, null);
            }
            catch( InvalidCoordinatesForCurrentGameException e ){}
        }
        while( Arrays.stream(gameToRefillButNoTiles.getTileSack().getRemaining()).sum() != 0 )
            gameToRefillButNoTiles.getTileSack().pop();

        gameToRefillButNotEnoughTiles = new Game(2);
        gameToRefillButNotEnoughTiles.addPlayer("Picci",(Message message)->{});
        gameToRefillButNotEnoughTiles.addPlayer("Roma",(Message message)->{});
        gameToRefillButNotEnoughTiles.init();
        for( Coordinates c : gameToRefillButNotEnoughTiles.getGameBoard().getCoords() ){
            try{
                gameToRefillButNotEnoughTiles.getGameBoard().setTile(c, null);
            }
            catch( InvalidCoordinatesForCurrentGameException e ){}
        }
        while( Arrays.stream(gameToRefillButNotEnoughTiles.getTileSack().getRemaining()).sum() >= 2 )
            gameToRefillButNotEnoughTiles.getTileSack().pop();
    }

    @Test
    public void testRefillGameBoard_ToRefill() throws EmptySackException {
        assertTrue( gameToRefill.getGameBoard().toRefill() );
        assertTrue( gameToRefill.refillGameBoard() );
        for( Coordinates c : gameToRefill.getGameBoard().getCoords() ) {
            try {
                assertTrue( gameToRefill.getGameBoard().getTile(c) != null );
            }
            catch(InvalidCoordinatesForCurrentGameException e){}
        }
    }

    @Test
    public void testRefillGameBoard_ToRefillButNoTiles() throws EmptySackException {
        assertTrue( gameToRefillButNoTiles.getGameBoard().toRefill() );
        Assert.assertThrows(EmptySackException.class, () -> gameToRefillButNoTiles.refillGameBoard() );
    }

    @Test
    public void testRefillGameBoard_ToRefillButNotEnoughTiles() throws EmptySackException {
        boolean oneNull = false;
        assertTrue( gameToRefillButNotEnoughTiles.getGameBoard().toRefill() );
        Assert.assertThrows(EmptySackException.class, () -> gameToRefillButNotEnoughTiles.refillGameBoard() );
        for( Coordinates c : gameToRefill.getGameBoard().getCoords() ) {
            try {
                if( gameToRefill.getGameBoard().getTile(c) == null ){
                    oneNull = true;
                    break;
                }
            }
            catch(InvalidCoordinatesForCurrentGameException e){}
        }
        assertTrue( oneNull );
    }

    @Test
    public void testAddPlayer(){
        assertEquals(game.getPlayer(0).getUsername(),"Picci");
        assertEquals(game.getPlayer(1).getUsername(),"Roma");
        assertEquals(game.getCurrentPlayer(),0);
        game.nextPlayer();
        assertEquals(game.getCurrentPlayer(),1);
    }

    @Test
    public void testDisconnect() throws UsernameNotFoundException {
        assertEquals(game.getNumOfActivePlayers(), 2);
        game.disconnect("Picci");
        assertEquals(game.getNumOfActivePlayers(), 1);
    }

    @Test
    public void testKick() throws UsernameNotFoundException {
        assertEquals(game.getNumOfActivePlayers(), 2);
        game.kick("Picci");
        assertEquals(game.getNumOfActivePlayers(), 1);
    }

    @Test
    public void testGetCurrentPlayer(){
        assertNotNull(game.getCurrentPlayer());
        assertTrue(0 <= game.getCurrentPlayer() && game.getCurrentPlayer() < game.getNumOfPlayers());
    }

    @Test
    public void testGetGoals() throws CloneNotSupportedException {
        assertNotNull(game.getGoals());
    }

    @Test
    public void testNextPlayer(){
        int before = game.getCurrentPlayer();
        game.nextPlayer();
        int after = game.getCurrentPlayer();
        assertFalse(before == after);
    }

    @Test
    public void testGetPlayer() throws InvalidIndexException {
        Player p1 = new Player("Picci");
        p1.init(PrivateGoal.getPrivateGoals(2)[0]);
        Player p2 = new Player("Roma");
        p2.init(PrivateGoal.getPrivateGoals(2)[0]);

        assertEquals(game.getPlayer(0).getUsername(), p1.getUsername());
        assertEquals(game.getPlayer(1).getUsername(), p2.getUsername());
    }

    @Test
    public void test_InvalidIndexException() throws InvalidIndexException {
        Assert.assertThrows(InvalidIndexException.class, () -> game.getPlayer(-1));
    }

    @Test
    public void testGameConstructorAndPrivateMethods(){
        assertTrue( game != null );
    }

}
