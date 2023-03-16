package it.polimi.ingsw.Model;

public class TileSack {
    private int[] remaining;    // Index is the corresponding color in TileColor
    private final static int LEN = TileColor.values().length;   // length of the array
    private final static int NUM_OF_TILES_PER_COLOR = 22;

    public TileSack(){
        // initialize the array with the maximum value for every element
        for(int i = 0; i < LEN; i++)
            remaining[i] = NUM_OF_TILES_PER_COLOR;
    }

    public Tile pop(){
        // Check that returns a Tile object != null
        //Todo: how to generate the color i want to pop?
    }

}
