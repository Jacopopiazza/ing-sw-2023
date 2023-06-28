package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;

/**
 * The Shelf class represents a shelf in the game where tiles can be placed.
 */
public class Shelf implements Cloneable{
    private final Tile[][] SHELF;   // The matrix representing the shelf

    /**
     * Constructs a new {@code Shelf} object from a given matrix of tiles.
     */
    private Shelf(Tile[][] shelf) {
        this.SHELF = new Tile[Shelf.getRows()][Shelf.getColumns()];
        for( int i = 0; i<Shelf.getRows(); i++ ) {
            for( int j = 0; j<Shelf.getColumns(); j++ ) {
                this.SHELF[i][j] = shelf[i][j] == null ? null : shelf[i][j].clone();
            }
        }
    }
    /**
     * Constructs a new {@code Shelf} object with an empty shelf.
     */
    public Shelf() {
        // initialize the matrix with null values
        SHELF = new Tile[Shelf.getRows()][Shelf.getColumns()];
    }

    /**
     * Adds a tile to the specified column of the shelf.
     *
     * @param t      the tile to be added
     * @param column the column where the tile should be added
     * @throws NoTileException                if the tile is null
     * @throws IllegalColumnInsertionException if the column is already fully filled with a tile in the top row
     * @throws ColumnOutOfBoundsException     if the column index is out of bounds
     */
    public void addTile(Tile t, int column) throws NoTileException, IllegalColumnInsertionException, ColumnOutOfBoundsException{
        if( t == null ) {
            throw new NoTileException();
        }
        if( ( column < 0 ) || ( column >= Shelf.getColumns() ) ) {
            throw new ColumnOutOfBoundsException();
        }

        // If the first row is already filled with a tile, the column is fully filled
        if( SHELF[0][column] != null ) {
            throw new IllegalColumnInsertionException();
        }

        // start analysing from the bottom
        int row = Shelf.getRows() - 1;

        while( ( SHELF[row][column] != null ) && ( row >= 0 ) ) row--;

        SHELF[row][column] = t.clone();
    }

    /**
     * Retrieves the tile at the specified coordinates from the shelf.
     *
     * @param c the coordinates of the tile to retrieve
     * @return the tile at the specified coordinates
     * @throws ColumnOutOfBoundsException if the coordinates are out of bounds
     */
    public Tile getTile(Coordinates c) throws ColumnOutOfBoundsException{
        if( ( c.getROW() < 0 ) || ( c.getCOL() < 0 ) || ( c.getROW() >= Shelf.getRows() ) || ( c.getCOL() >= Shelf.getColumns() ) ) {
            throw new ColumnOutOfBoundsException();
        }
        return SHELF[c.getROW()][c.getCOL()];
    }

    /**
     * Returns the remaining space in the specified column of the shelf.
     *
     * @param column the column index
     * @return the number of empty slots in the column
     * @throws ColumnOutOfBoundsException if the column index is out of bounds
     */
    public int remainingSpaceInColumn(int column) throws ColumnOutOfBoundsException{
        if(column<0 || column>=Shelf.getColumns()) throw new ColumnOutOfBoundsException();

        int result = 0;
        for(int i=0;i<Shelf.getRows() && this.SHELF[i][column]!=null; i++) {
            result++;
        }
        return Shelf.getRows()-result;
    }

    /**
     * Returns the number of columns in the shelf.
     *
     * @return the number of columns in the shelf
     */
    public static int getColumns() {
        return Config.getInstance().getShelfColumns();
    }

    /**
     * Returns the number of rows in the shelf.
     *
     * @return the number of rows in the shelf
     */
    public static int getRows() {
        return Config.getInstance().getShelfRows();
    }

    /**
     * Creates and returns a copy of the Shelf object.
     *
     * @return a new Shelf object that is a copy of this shelf
     */
    @Override
    public Shelf clone() {
        return new Shelf(this.SHELF);
    }

    /**
     * Checks if the shelf is full, i.e., all columns in the top row are filled with tiles.
     *
     * @return {@code true} if the shelf is full, {@code false} otherwise
     */
    public boolean isFull() {
        for(int i = 0; i < getColumns(); i++) {
            if(SHELF[0][i] == null)
                return false;
        }
        return true;
    }

}

