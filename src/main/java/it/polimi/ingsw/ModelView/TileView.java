package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Tile;
import it.polimi.ingsw.Model.TileColor;

import java.io.Serializable;

public class TileView implements Serializable {

    private static final long serialVersionUID=1L;

    private final TileColor COLOR;

    private final boolean pickable;
    private final int ID;

    public TileView(Tile tile, boolean pickable){
        this.ID = tile.getId();
        this.COLOR = tile.getColor();
        this.pickable = pickable;
    }

    public TileView(Tile tile){
        this.ID = tile.getId();
        this.COLOR = tile.getColor();
        this.pickable = false;
    }

    public TileColor getCOLOR() {
        return COLOR;
    }

    public int getID() {
        return ID;
    }

    public boolean isPickable(){return pickable;}

    @Override
    public boolean equals(Object o) {
        if( this == o )
            return true;
        if( ( o == null ) || ( this.getClass() != o.getClass() ) )
            return false;
        TileView tile = (TileView) o;
        return ( tile.ID == this.ID ) && ( tile.COLOR == this.COLOR );
    }

}
