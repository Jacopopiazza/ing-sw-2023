package it.polimi.ingsw.Model;

/**
 * The Tile class represents a tile in the game.
 */
public class Tile implements Cloneable{
    private final TileColor COLOR;  // The color of the tile
    private final int ID;           // The ID of the tile

    /**
     * Constructs a new Tile object with the specified color and ID.
     *
     * @param color the color of the tile
     * @param id    the ID of the tile
     */
    public Tile(TileColor color, int id) {
        this.COLOR = color;
        this.ID = id;
    }

    /**
     * Gets the color of the tile.
     *
     * @return the color of the tile
     */
    public TileColor getColor() {
        return COLOR;
    }

    /**
     * Gets the ID of the tile.
     *
     * @return the ID of the tile
     */
    public int getId() {
        return ID;
    }

    /**
     * Creates and returns a copy of the Tile object.
     *
     * @return a new Tile object that is a copy of this tile
     */
    @Override
    public Tile clone() {
        return new Tile(this.COLOR,this.ID);
    }

    /**
     * Checks if this tile is equal to the specified object.
     * Two tiles are considered equal if they have the same ID and color.
     *
     * @param o the object to compare with this tile
     * @return {@code true} if the specified object is equal to this tile, {@code false} otherwise
     */
    @Override
    public boolean equals(Object o) {
        if( this == o )
            return true;
        if( ( o == null ) || ( this.getClass() != o.getClass() ) )
            return false;
        Tile tile = (Tile) o;
        return ( tile.ID == this.ID ) && ( tile.COLOR == this.COLOR );
    }

}

