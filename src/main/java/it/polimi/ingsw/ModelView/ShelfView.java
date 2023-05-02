package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Exceptions.ColumnOutOfBoundsException;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.Tile;

import java.io.Serializable;

public class ShelfView implements Serializable {
    private static final long serialVersionUID=1L;

    private final TileView[][] SHELF;

    public ShelfView(Shelf shelf){
        this.SHELF = new TileView[Shelf.getRows()][Shelf.getColumns()];
        for(int i = 0; i < Shelf.getRows(); i++){
            for(int j = 0; j < Shelf.getColumns(); j++){
                this.SHELF[i][j] = new TileView(shelf.getTile(new Coordinates(i,j)));
            }
        }
    }

    public TileView getTile(Coordinates c) throws ColumnOutOfBoundsException {

        if( ( c.getROW() < 0 ) || ( c.getCOL() < 0 ) || ( c.getROW() >= Shelf.getRows() ) || ( c.getCOL() >= Shelf.getColumns() ) ){
            throw new ColumnOutOfBoundsException();
        }

        return SHELF[c.getROW()][c.getCOL()];
    }

    public boolean isFull(){

        for( int j = 0; j < Shelf.getColumns(); j++ ){
            if( SHELF[0][j] == null ) return false;
        }

        return true;
    }
}
