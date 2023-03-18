package it.polimi.ingsw.Model;

import java.util.Arrays;
import java.util.Random;

public class TileSack {
    private int[] remaining;    // Index is the corresponding color in TileColor
    private final static int LEN = TileColor.values().length;   // length of the array
    private final static int NUM_OF_TILES_PER_COLOR = 22;

    public TileSack(){
        // initialize the array with the maximum value for every element
        for(int i = 0; i < LEN; i++)
            remaining[i] = NUM_OF_TILES_PER_COLOR;
    }

    // Generate a Tile with a random color and pops it from the sack
    public Tile pop() throws InvalidColorException{
        int random_color_index = 0;
        // Generate a number from 0 to 'remaining Tiles' - 1
        random_color_index = new Random().nextInt(Arrays.stream(remaining).sum());

        for(int i = 0; i < LEN; i++){
            if(random_color_index >= i * NUM_OF_TILES_PER_COLOR &&
                    random_color_index < (i + 1) * NUM_OF_TILES_PER_COLOR) {
                try {
                    // Pop that color from the tileSack
                    remaining[i]--;
                    // Return the corresponding Tile
                    return new Tile(TileColor.values()[random_color_index]);
                } catch (InvalidColorException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

}
