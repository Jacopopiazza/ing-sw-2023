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
    public Object clone(){
        return new Tile(this.COLOR,this.ID);
    }


}

