package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;

/**
 * The Angles class represents a global goal called "Angles" in the game MyShelfie.
 * This goal checks if the tiles placed at the four corners of a shelf have the same color.
 * If all four corner tiles have the same color, the goal is satisfied.
 */
public class Angles extends GlobalGoal {

    /**
     * Constructs an Angles global goal with the specified number of players.
     *
     * @param people the number of players.
     * @throws InvalidNumberOfPlayersException if the number of players is invalid.
     */
    public Angles(int people) throws InvalidNumberOfPlayersException {
        super(people, 8);
        this.description = "Four tiles of the same type in the four " +
                "corners of the bookshelf.";
    }

    /**
     * Checks if the given shelf satisfies the Angles global goal.
     *
     * @param s the shelf to check.
     * @return true if the shelf satisfies the Angles global goal, false otherwise.
     * @throws MissingShelfException if the shelf is missing or null.
     */
    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if( s == null ){
            throw new MissingShelfException();
        }

        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        if( ( s.getTile(new Coordinates(0,0)) == null )
                || ( s.getTile(new Coordinates(0,c-1)) == null )
                || ( s.getTile(new Coordinates(r-1,0)) == null )
                || ( s.getTile(new Coordinates(r-1,c-1)) == null )
        ) return false;

        if( s.getTile(new Coordinates(0,0)).getColor().equals(s.getTile(new Coordinates(0,c-1)).getColor())
                && s.getTile(new Coordinates(0,c-1)).getColor().equals(s.getTile(new Coordinates(r-1,0)).getColor())
                && s.getTile(new Coordinates(r-1,0)).getColor().equals(s.getTile(new Coordinates(r-1,c-1)).getColor())
        ) return true;

        return false;
    }

}
