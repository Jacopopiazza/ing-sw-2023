package it.polimi.ingsw.Model;
import it.polimi.ingsw.Exceptions.*;
import java.util.*;

public class Tile implements Cloneable{
    private final TileColor COLOR;
    private final int ID;
    private static int nextId[] = {0};

    public Tile(TileColor color) {
        this.COLOR = color;
        this.ID = nextId[COLOR.ordinal()];
        nextId[COLOR.ordinal()]++;
    }

    private Tile(TileColor color, int ID){
        this.COLOR = color;
        this.ID = ID;
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

