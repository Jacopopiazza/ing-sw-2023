package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.TileColor;

/**
 * The EightTiles class represents a global goal called "Eight Tiles" of the game.
 * This goal checks if a specified number of tiles of the same color are present on the shelf.
 * It extends the {@code GlobalGaol} abstract class.
 */
public class EightTiles extends GlobalGoal {
    /**
     * Constructs a new {@code EightTiles} global goal with the specified parameters.
     *
     * @param people the number of players.
     * @throws InvalidNumberOfPlayersException if the number of players is invalid.
     */
    public EightTiles(int people) throws InvalidNumberOfPlayersException {
        super(people, 9);
        this.description = "Eight tiles of the same type. There’s no " +
                "restriction about the position of these tiles.";
    }

    /**
     * Checks if the given shelf satisfies the EightTiles global goal.
     * The shelf must have a specified number of tiles of the same color.
     *
     * @param s the shelf to check.
     * @return true if the shelf satisfies the EightTiles global goal, false otherwise.
     * @throws MissingShelfException if the shelf is missing or null.
     */
    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        int numOfEqualTiles = 8;
        int[] counters = new int[TileColor.values().length];

        for( int i = 0; i < r; i++ ){
            for( int j = 0; j < c; j++ ){
                Coordinates coord = new Coordinates(i, j);
                //if not null, counting one more tile of its color
                if( s.getTile(coord) != null ){
                    counters[s.getTile(coord).getColor().ordinal()]++;
                    if( counters[ s.getTile(coord).getColor().ordinal() ] >= numOfEqualTiles )
                        return true;
                }

            }
        }
        return false;
    }

}
