package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Tile;
import it.polimi.ingsw.Model.TileColor;

import java.io.Serial;
import java.io.Serializable;

/**
 * The {@code TileView} class represents the immutable version of the {@link it.polimi.ingsw.Model.Tile}.
 * It provides a snapshot of a tile in a serializable format.
 */
public class TileView implements Serializable {
    @Serial
    private static final long serialVersionUID=1L;
    private final TileColor color;
    private final int id;

    /**
     * Constructs a new {@code TileView} object based on the given {@code Tile} object.
     *
     * @param tile the {@link Tile} object to create the view from
     */

    public TileView(Tile tile){
        this.id = tile.getId();
        this.color = tile.getColor();
    }

    /**
     * Retrieves the color of the tile.
     *
     * @return the {@link TileColor} of the tile
     */

    public TileColor getColor() {
        return color;
    }

    /**
     * Retrieves the ID of the tile.
     *
     * @return the ID of the tile
     */

    public int getId() {
        return id;
    }

    /**
     * Checks if this TileView is equal to another object.
     * A tile is equal to another tile if they have the same ID and color.
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
        return ( tile.id == this.id) && ( tile.color == this.color);
    }

}
