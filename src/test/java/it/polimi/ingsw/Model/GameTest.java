package it.polimi.ingsw.Model;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;

public class GameTest extends TestCase {
    Game game;

    @Before
    public void setUp() throws FileNotFoundException {
        String a[] = {"a","b"};
        game = new Game(a);
    }

    @Test
    public void testGameConstructorAndPrivateMethods(){
        assertTrue( game != null );
    }

}
