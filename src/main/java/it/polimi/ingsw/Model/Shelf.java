package it.polimi.ingsw.Model;

import java.util.Optional;

//to re-use a Shelf, it needs to be emptied through the empty() method
public class Shelf {
    private final Optional<Tile>[][];
    private final int columns;
    private final int rows;

    public void addTile(Tile tile, int column){

    }

    public Optional<Tile> getTile(Coordinates c){

    }

    public int getColumns(){
        return columns;
    }

    public int getRows(){
        return rows;
    }

    public void empty(){

    }
}
