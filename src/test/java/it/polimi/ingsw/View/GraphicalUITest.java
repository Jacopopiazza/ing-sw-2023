package it.polimi.ingsw.View;

import junit.framework.TestCase;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

public class GraphicalUITest extends TestCase {

    @Test
    public void testRun(){
        new GraphicalUI().run();
        while(true);
    }
}
