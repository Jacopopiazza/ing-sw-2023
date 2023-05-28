package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Tile;
import it.polimi.ingsw.Model.TileColor;

import java.io.Serializable;

public class TileView implements Serializable {

    private static final long serialVersionUID=1L;

    private final TileColor COLOR;
    private final int ID;

    public TileView(Tile tile){
        this.ID = tile.getId();
        this.COLOR = tile.getColor();
    }

    public TileColor getCOLOR() {
        return COLOR;
    }

    public int getID() {
        return ID;
    }

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
