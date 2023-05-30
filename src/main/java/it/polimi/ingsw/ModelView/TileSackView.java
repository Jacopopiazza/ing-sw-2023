package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.Model.TileSack;

import java.io.Serializable;

/**
 * The TileSackView class represents a view of the tile sack in the game.
 * It provides a snapshot of the remaining tiles in the sack in a serializable format.
 */

public class TileSackView implements Serializable {
    private static final long serialVersionUID=1L;
    private final int[] remaining;
    private final static int LEN = TileColor.values().length;

    /**
     * Constructs a new TileSackView object based on the given TileSack object.
     *
     * @param tileSack the TileSack object to create the view from
     */

    public TileSackView(TileSack tileSack){
        this.remaining = tileSack.getRemaining().clone();
    }

    /**
     * Retrieves a copy of the remaining tiles in the tile sack.
     *
     * @return an array representing the remaining tiles in the sack
     */

    public int[] getRemaining(){
        return this.remaining.clone();
    }
}
