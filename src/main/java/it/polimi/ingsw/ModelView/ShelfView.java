package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Exceptions.ColumnOutOfBoundsException;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;

import java.io.Serializable;

/**
 * The {@code ShelfView} class represents the immutable version of the {@link it.polimi.ingsw.Model.Shelf}.
 * It provides a snapshot of the player's shelf in a serializable format.
 */
public class ShelfView implements Serializable {
    private static final long serialVersionUID=1L;
    private final TileView[][] shelf;

    /**
     * Constructs a new {@code ShelfView} object based on the given {@link it.polimi.ingsw.Model.Shelf} object.
     *
     * @param shelf the {@link it.polimi.ingsw.Model.Shelf} object to create the view from
     */
    public ShelfView(Shelf shelf){
        this.shelf = new TileView[Shelf.getRows()][Shelf.getColumns()];
        for(int i = 0; i < Shelf.getRows(); i++){
            for(int j = 0; j < Shelf.getColumns(); j++){
                this.shelf[i][j] = ( shelf.getTile(new Coordinates(i,j)) == null ) ? null : new TileView( shelf.getTile(new Coordinates(i,j) ) );
            }
        }
    }

    /**
     * Retrieves the {@link TileView} object at the specified coordinates on the shelf.
     *
     * @param c the coordinates of the tile to retrieve
     * @return the {@link TileView} object at the specified coordinates
     * @throws ColumnOutOfBoundsException if the specified coordinates are out of bounds
     */
    public TileView getTile(Coordinates c) throws ColumnOutOfBoundsException {

        if( ( c.getROW() < 0 ) || ( c.getCOL() < 0 ) || ( c.getROW() >= Shelf.getRows() ) || ( c.getCOL() >= Shelf.getColumns() ) ){
            throw new ColumnOutOfBoundsException();
        }

        return shelf[c.getROW()][c.getCOL()];
    }

}
