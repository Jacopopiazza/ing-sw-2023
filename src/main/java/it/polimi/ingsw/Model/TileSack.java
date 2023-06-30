package it.polimi.ingsw.Model;

import it.polimi.ingsw.Utilities.Config;

import java.util.Arrays;
import java.util.Random;

/**
 * The TileSack class represents a sack of tiles in the game.
 * It keeps track of the remaining tiles of each color and provides methods to retrieve and remove tiles from the sack.
 */
public class TileSack {
    private final int[] remaining;                                    // Index is the corresponding color in TileColor
    private final static int LEN = TileColor.values().length;   // length of the array

    /**
     * Constructs a new {@code TileSack} object with the initial number of tiles per color.
     */
    public TileSack() {
        remaining = new int[LEN];
        // initialize the array with the maximum value for every element
        for( int i = 0; i < LEN; i++ ) remaining[i] = Config.getInstance().getNumOfTilesPerColor();
    }

    /**
     * Generates a random {@code TileColor} and removes it from the sack.
     *
     * @return the {@link Tile} object with the random color, or null if the sack is empty.
     */
    public Tile pop() {
        int random_color_index = 0;

        if(Arrays.stream(remaining).sum() == 0) return null;

        // Generate a number from 0 to 'remaining Tiles' - 1
        random_color_index = new Random().nextInt(Arrays.stream(remaining).sum());

        int top_bound = 0, bottom_bound = 0;
        for( int i = 0; i < LEN; i++ ) {
            top_bound += remaining[i];
            if( ( random_color_index >= bottom_bound ) && ( random_color_index < top_bound ) ) {
                // Pop that color from the tileSack
                remaining[i]--;
                // Return the corresponding Tile
                return new Tile(TileColor.values()[i], Config.getInstance().getNumOfTilesPerColor()-remaining[i]-1);
            }
            else bottom_bound += remaining[i];
        }
        return null;
    }

    /**
     * Gets the array of remaining tiles for each color.
     *
     * @return an array of integers representing the remaining tiles for each color.
     */
    public int[] getRemaining() {
        return this.remaining;
    }

}
