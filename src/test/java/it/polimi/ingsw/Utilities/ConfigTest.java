package it.polimi.ingsw.Utilities;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ConfigTest {
    @Test
    public void testGetInstance() {
        assertNotNull(Config.getInstance());
    }
}