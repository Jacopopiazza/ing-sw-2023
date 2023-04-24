package it.polimi.ingsw.Model.Utilities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testGetInstance() {
        assertNotNull(Config.getInstance());
    }
}