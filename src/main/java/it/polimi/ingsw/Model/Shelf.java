package it.polimi.ingsw.Model;
import it.polimi.ingsw.Exceptions.*;


public class Shelf implements Cloneable{
    private final Tile[][] SHELF;
    private static final int COLUMNS = 5;
    private static final int ROWS = 6;

    private Shelf(Tile[][] shelf){
        this.SHELF = shelf.clone();
    }

    public Shelf(){
        // initialize the matrix with null values
        SHELF = new Tile[ROWS][COLUMNS];
    }

    public void addTile(Tile t, int column) throws IllegalColumnInsertionException, ColumnOutOfBoundsException{

        if(column < 0 || column >= COLUMNS) throw new ColumnOutOfBoundsException();

        // If the first row is already filled with a tile, the column is fully filled
        if(SHELF[0][column] != null)
            throw new IllegalColumnInsertionException();

        // start analysing from the bottom
        int row = ROWS - 1;
        while(SHELF[row][column] != null && row >= 0){
            row--;
        }

        SHELF[row][column] = (Tile)t.clone();
    }

    public Tile getTile(Coordinates c) throws ColumnOutOfBoundsException{
        if(c.getX() < 0 || c.getY() < 0 || c.getX() >= ROWS || c.getY() >= COLUMNS) throw new ColumnOutOfBoundsException();

        return SHELF[c.getX()][c.getY()];
    }

    public int getColumns(){
        return COLUMNS;
    }

    public int getRows(){
        return ROWS;
    }

    @Override
    protected Object clone() {
        return new Shelf(this.SHELF);
    }

}

