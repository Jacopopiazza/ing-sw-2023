package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Tile;
import it.polimi.ingsw.Model.TileColor;

import java.io.Serializable;

/**
 * The TileView class represents a view of a tile in the game.
 * It provides a snapshot of the tile's color and ID in a serializable format.
 */
public class TileView implements Serializable {
    private static final long serialVersionUID=1L;
    private final TileColor COLOR;
    private final int ID;

    /**
     * Constructs a new TileView object based on the given Tile object.
     *
     * @param tile the Tile object to create the view from
     */

    public TileView(Tile tile){
        this.ID = tile.getId();
        this.COLOR = tile.getColor();
    }

    /**
     * Retrieves the color of the tile.
     *
     * @return the color of the tile
     */

    public TileColor getCOLOR() {
        return COLOR;
    }

    /**
     * Retrieves the ID of the tile.
     *
     * @return the ID of the tile
     */

    public int getID() {
        return ID;
    }

    /**
     * Checks if this TileView is equal to another object.
     *
     * @param o the object to compare to
     * @return true if the objects are equal, false otherwise
     */

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
