package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.InvalidCoordinatesForCurrentGameException;
import it.polimi.ingsw.Exceptions.InvalidIndexException;
import it.polimi.ingsw.Exceptions.InvalidScoreException;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.EventListener;

public class GameTest extends TestCase {
    Game game;
    Game gameToRefill;
    Game gameToRefillButNoTiles;
    Game gameToRefillButNotEnoughTiles;

    @Before
    public void setUp() throws FileNotFoundException {
        game = new Game(2);

        gameToRefill = new Game(2);
        for( Coordinates c : gameToRefill.getGameBoard().getCoords() ){
            try{
                gameToRefill.getGameBoard().setTile(c, null);
            }
            catch( InvalidCoordinatesForCurrentGameException e ){}
        }

        gameToRefillButNoTiles = new Game(2);
        for( Coordinates c : gameToRefillButNoTiles.getGameBoard().getCoords() ){
            try{
                gameToRefillButNoTiles.getGameBoard().setTile(c, null);
            }
            catch( InvalidCoordinatesForCurrentGameException e ){}
        }
        while( Arrays.stream(gameToRefillButNoTiles.getTileSack().getRemaining()).sum() != 0 )
            gameToRefillButNoTiles.getTileSack().pop();

        gameToRefillButNotEnoughTiles = new Game(2);
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
    public void testRefillGameBoard_ToRefill(){
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
    public void testRefillGameBoard_ToRefillButNoTiles(){
        assertTrue( gameToRefillButNoTiles.getGameBoard().toRefill() );
        assertFalse( gameToRefillButNoTiles.refillGameBoard() );
    }

    @Test
    public void testRefillGameBoard_ToRefillButNotEnoughTiles(){
        boolean oneNull = false;
        assertTrue( gameToRefillButNotEnoughTiles.getGameBoard().toRefill() );
        assertTrue( gameToRefillButNotEnoughTiles.refillGameBoard() );
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
        assertEquals(game.addPlayer("Roma", null), 1);
        assertEquals(game.addPlayer("Fra", null), 2);
    }

    @Test
    public void testDisconnect(){
        game.addPlayer("Roma", new EventListener() {
            @Override
            public String toString() {
                return super.toString();
            }
        });

        game.addPlayer("Fra", new EventListener() {
            @Override
            public String toString() {
                return super.toString();
            }
        });

        assertEquals(game.getNumOfActivePlayers(), 2);

        game.disconnect("Fra");

        assertEquals(game.getNumOfActivePlayers(), 1);
    }

    @Test
    public void testKick(){
        game.addPlayer("Roma", new EventListener() {
            @Override
            public String toString() {
                return super.toString();
            }
        });

        game.addPlayer("Fra", new EventListener() {
            @Override
            public String toString() {
                return super.toString();
            }
        });

        assertEquals(game.getNumOfActivePlayers(), 2);

        game.kick("Fra");

        assertEquals(game.getNumOfActivePlayers(), 1);
    }

    @Test
    public void testGetCurrentPlayer(){
        game.addPlayer("J", null);
        game.addPlayer("Roma", null);

        assertNotNull(game.getCurrentPlayer());
        assertTrue(0 <= game.getCurrentPlayer() && game.getCurrentPlayer() < 2);
    }

    @Test
    public void testGetGoals() throws CloneNotSupportedException {
        assertNotNull(game.getGoals());
    }

    @Test
    public void testNextPlayer(){
        game.addPlayer("J", new EventListener() {
            @Override
            public String toString() {
                return super.toString();
            }
        });
        game.addPlayer("Roma", new EventListener() {
            @Override
            public String toString() {
                return super.toString();
            }
        });
        int before = game.getCurrentPlayer();
        game.nextPlayer();
        int after = game.getCurrentPlayer();
        assertFalse(before == after);
    }

    @Test
    public void testGetPlayer() throws InvalidIndexException {
        Player p1 = new Player("Roma");
        Player p2 = new Player("J");

        game.addPlayer("Roma", null);
        game.addPlayer("J", null);

        assertEquals(game.getPlayer(0).getNickname(), p1.getNickname());
        assertEquals(game.getPlayer(1).getNickname(), p2.getNickname());
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
