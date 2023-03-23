package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;


public class Shelf implements Cloneable{
    private final Tile[][] SHELF;

    private Shelf(Tile[][] shelf){
        this.SHELF = new Tile[Config.getInstance().getShelfRows()][Config.getInstance().getShelfColumns()];
        for(int i=0;i<Config.getInstance().getShelfRows();i++){
            for(int j=0;j<Config.getInstance().getShelfColumns();j++){
                this.SHELF[i][j] = (Tile) shelf[i][j].clone();
            }
        }
    }

    public Shelf(){
        // initialize the matrix with null values
        SHELF = new Tile[Config.getInstance().getShelfRows()][Config.getInstance().getShelfColumns()];
    }

    public void addTile(Tile t, int column) throws IllegalColumnInsertionException, ColumnOutOfBoundsException{

        if(column < 0 || column >= Config.getInstance().getShelfColumns()) throw new ColumnOutOfBoundsException();

        // If the first row is already filled with a tile, the column is fully filled
        if(SHELF[0][column] != null)
            throw new IllegalColumnInsertionException();

        // start analysing from the bottom
        int row = Config.getInstance().getShelfRows() - 1;
        while(SHELF[row][column] != null && row >= 0){
            row--;
        }

        SHELF[row][column] = (Tile)t.clone();
    }

    public Tile getTile(Coordinates c) throws ColumnOutOfBoundsException{
        if(c.getX() < 0 || c.getY() < 0 || c.getX() >= Config.getInstance().getShelfRows() || c.getY() >= Config.getInstance().getShelfColumns()) throw new ColumnOutOfBoundsException();

        return SHELF[c.getX()][c.getY()];
    }

    public static int getColumns(){
        return Config.getInstance().getShelfColumns();
    }

    public static int getRows(){
        return Config.getInstance().getShelfRows();
    }

    @Override
    public Object clone() {
        return new Shelf(this.SHELF);
    }

}

