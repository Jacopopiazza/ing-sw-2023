package it.polimi.ingsw;

import org.junit.Test;

import static org.junit.Assert.*;

public class TupleTest {

    @Test
    public void getFirst() {

        Integer first = 1;
        Integer second = 2;

        Tuple<Integer, Integer> tuple = new Tuple<>(first, second);
        assertEquals(first, tuple.getFirst());
    }

    @Test
    public void getSecond() {
        Integer first = 1;
        Integer second = 2;

        Tuple<Integer, Integer> tuple = new Tuple<>(first, second);
        assertEquals(second, tuple.getSecond());
    }
}