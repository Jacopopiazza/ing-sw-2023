package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.InvalidCoordinatesForCurrentGameException;
import junit.framework.TestCase;
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
        String a[] = {"a","b"};

        game = new Game(a);

        gameToRefill = new Game(a);
        for( Coordinates c : gameToRefill.getGameBoard().getCoords() ){
            try{
                gameToRefill.getGameBoard().setTile(c, null);
            }
            catch( InvalidCoordinatesForCurrentGameException e ){}
        }

        gameToRefillButNoTiles = new Game(a);
        for( Coordinates c : gameToRefillButNoTiles.getGameBoard().getCoords() ){
            try{
                gameToRefillButNoTiles.getGameBoard().setTile(c, null);
            }
            catch( InvalidCoordinatesForCurrentGameException e ){}
        }
        while( Arrays.stream(gameToRefillButNoTiles.getTileSack().getRemaining()).sum() != 0 )
            gameToRefillButNoTiles.getTileSack().pop();

        gameToRefillButNotEnoughTiles = new Game(a);
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
    public void testGameConstructorAndPrivateMethods(){
        assertTrue( game != null );
    }

}
