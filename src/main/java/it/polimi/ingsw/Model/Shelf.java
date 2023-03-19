package it.polimi.ingsw.Model;

public class Shelf implements Cloneable{
    private final Tile[][] SHELF;
    private static final int COLUMNS = 5;
    private static final int ROWS = 6;

    public Shelf(){
        // initialize the matrix with null values
        SHELF = new Tile[ROWS][COLUMNS];
    }

    public void addTile(Tile t, int column){
        // If the first row is already filled with a tile, the column is fully filled
        if(SHELF[0][column] != null)
            throw new IllegalColumnInsertionException();

        // start analysing from the bottom
        int row = ROWS - 1;
        while(SHELF[row][column] != null && row >= 0){
            row--;
        }

        // Todo: Do i have to pass the reference or a copy?
        SHELF[row][column] = t;
    }

    public Tile getTile(Coordinates c){
        return SHELF[c.getX()][c.getY()];
    }

    public int getColumns(){
        return COLUMNS;
    }

    public int getRows(){
        return ROWS;
    }

    @Override
    protected Shelf clone() throws CloneNotSupportedException {
        return (Shelf) super.clone();
    }

    public Shelf getShelf() throws CloneNotSupportedException {
        return this.clone();
    }
}

