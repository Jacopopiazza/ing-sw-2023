package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.Model.TileSack;

import java.io.Serializable;

public class TileSackView implements Serializable {
    private static final long serialVersionUID=1L;
    private final int[] remaining;
    private final static int LEN = TileColor.values().length;

    public TileSackView(TileSack tileSack){
        this.remaining = tileSack.getRemaining().clone();
    }

    public int[] getRemaining(){
        return this.remaining.clone();
    }
}
