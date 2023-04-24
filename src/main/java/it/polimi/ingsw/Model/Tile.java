package it.polimi.ingsw.Model;

public class Tile implements Cloneable{
    private final TileColor COLOR;
    private final int ID;

    public Tile(TileColor color, int id) {
        this.COLOR = color;
        this.ID = id;
    }

    public TileColor getColor() {
        return COLOR;
    }

    public int getId() {
        return ID;
    }

    @Override
    public Tile clone(){
        return new Tile(this.COLOR,this.ID);
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o )
            return true;
        if ( ( o == null ) || ( this.getClass() != o.getClass() ) )
            return false;
        Tile tile = (Tile) o;
        return ( tile.ID == this.ID ) && ( tile.COLOR == this.COLOR );
    }

}

