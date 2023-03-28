package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;


public class Shelf implements Cloneable{
    private final Tile[][] SHELF;

    private Shelf(Tile[][] shelf){
        this.SHELF = new Tile[Shelf.getRows()][Shelf.getColumns()];
        for( int i=0; i<Shelf.getRows(); i++ ){
            for( int j=0; j<Shelf.getColumns(); j++ ){
                this.SHELF[i][j] = shelf[i][j] == null ? null : shelf[i][j].clone();
            }
        }
    }

    public Shelf(){
        // initialize the matrix with null values
        SHELF = new Tile[Shelf.getRows()][Shelf.getColumns()];
    }

    // Test purpose
    protected void setTile(Coordinates c, TileColor color){
        SHELF[c.getX()][c.getY()] = new Tile(color, 0);
    }

    public void addTile(Tile t, int column) throws NoTileException, IllegalColumnInsertionException, ColumnOutOfBoundsException{
        if( t == null ){
            throw new NoTileException();
        }
        if( ( column < 0 ) || ( column >= Shelf.getColumns() ) ){
            throw new ColumnOutOfBoundsException();
        }

        // If the first row is already filled with a tile, the column is fully filled
        if( SHELF[0][column] != null ){
            throw new IllegalColumnInsertionException();
        }

        // start analysing from the bottom
        int row = Shelf.getRows() - 1;
        while( ( SHELF[row][column] != null ) && ( row >= 0 ) ){
            row--;
        }

        SHELF[row][column] = t.clone();
    }

    public Tile getTile(Coordinates c) throws ColumnOutOfBoundsException{
        if( ( c.getX() < 0 ) || ( c.getY() < 0 ) || ( c.getX() >= Shelf.getRows() ) || ( c.getY() >= Shelf.getColumns() ) ){
            throw new ColumnOutOfBoundsException();
        }
        return SHELF[c.getX()][c.getY()];
    }

    public boolean isFull(){
        for( int i=0; i<getRows(); i++ ){
            for( int j=0; j<getColumns(); j++ ){
                if( SHELF[i][j] == null ) return false;
            }
        }
        return true;
    }

    public static int getColumns(){
        return Config.getInstance().getShelfColumns();
    }

    public static int getRows(){
        return Config.getInstance().getShelfRows();
    }

    @Override
    public Shelf clone() {
        return new Shelf(this.SHELF);
    }

}

