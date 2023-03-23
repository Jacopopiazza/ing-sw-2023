package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Utilities.Config;

import java.util.Arrays;
import java.util.Random;

public class TileSack {
    private int[] remaining;                                    // Index is the corresponding color in TileColor
    private final static int LEN = TileColor.values().length;   // length of the array

    public TileSack(){
        // initialize the array with the maximum value for every element
        for(int i = 0; i < LEN; i++)
            remaining[i] = Config.getInstance().getNumOfTilesPerColor();
    }

    // Generate a Tile with a random color and pops it from the sack
    public Tile pop(){
        int random_color_index = 0;
        // Generate a number from 0 to 'remaining Tiles' - 1
        random_color_index = new Random().nextInt(Arrays.stream(remaining).sum());

        int top_bound = 0, bottom_bound = 0;
        for (int i = 0; i < LEN; i++) {
            top_bound += remaining[i];
            if (random_color_index >= bottom_bound && random_color_index < top_bound) {
                // Pop that color from the tileSack
                remaining[i]--;
                // Return the corresponding Tile
                return new Tile(TileColor.values()[i]);
            } else {
                bottom_bound += remaining[i];
            }
        }
        return null;
    }

}
