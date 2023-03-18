package it.polimi.ingsw.Model;

public class Tile {
    private final TileColor COLOR;
    private final int ID;
    private static int nextId[] = {0};

    public Tile(TileColor color) {
        this.COLOR = color;
        this.ID = nextId[COLOR.ordinal()];
        nextId[COLOR.ordinal()]++;
    }

    public TileColor getColor() {
        return COLOR;
    }

    public int getId() {
        return ID;
    }
}

