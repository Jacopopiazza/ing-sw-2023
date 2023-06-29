package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;

import java.util.*;
/**
 * The PrivateGoal class represents a private goal card in the game.
 */
public final class PrivateGoal {
    private final int id; // id used by the GUI
    private final Coordinates[] coords;   // The coordinates associated with the private goal

    /**
     * Constructs a new {@code PrivateGoal} object with the specified {@code Coordinates}.
     *
     * @param coords the array of {@link Coordinates} associated with the private goal
     */
    private PrivateGoal(Coordinates[] coords,int id) {
        this.coords = coords.clone();
        this.id = id;
    }

    /**
     * Gets the {@code Coordinates} associated with the private goal.
     *
     * @return an array of {@link Coordinates} representing the private goal
     */
    public Coordinates[] getCoordinates() {
        Coordinates[] temp = new Coordinates[coords.length];
        for(int i =0; i< coords.length;i++) temp[i] = coords[i].clone();
        return temp;
    }

    /**
     * Gets id associated with the private goal.
     *
     * @return an integer representing the private goal
     */
    public int getId(){
        return id;
    }

    /**
     * Generates and returns an array of {@code PrivateGoal}s based on the number of players.
     *
     * @param people the number of players
     * @return an array of {@link PrivateGoal}
     * @throws InvalidNumberOfPlayersException if the number of players is invalid
     */
    public static PrivateGoal[] getPrivateGoals(int people) throws InvalidNumberOfPlayersException {
        if( ( people <= 0 ) || ( people > Config.getInstance().getMaxNumberOfPlayers() ) ) {
            throw new InvalidNumberOfPlayersException();
        }

        PrivateGoal[] retValue = new PrivateGoal[people];

        List<Coordinates[]> allPrivateGoals = new ArrayList<Coordinates[]>(Config.getInstance().getPrivateGoals());
        Collections.shuffle(allPrivateGoals);

        for( int i = 0; i < people; i++ )
            retValue[i] = new PrivateGoal(allPrivateGoals.get(i),Config.getInstance().getPrivateGoals().indexOf(allPrivateGoals.get(i))+1);

        return retValue;
    }

    /**
     * Checks the tiles on the {@code Shelf} for this private goal.
     *
     * @param shelf the {@link Shelf} to check
     * @return the score based on the correctness of the tiles
     * @throws MissingShelfException       if the shelf is missing
     * @throws ColumnOutOfBoundsException if the column index is out of bounds
     */
    public int check(Shelf shelf) throws MissingShelfException, ColumnOutOfBoundsException {
        if( shelf == null ) {
            throw new MissingShelfException("Missing shelf");
        }

        int numOfCorrectTiles = 0;

        for( int i = 0; i < coords.length; i++ ) {
            if( (shelf.getTile(coords[i]) != null) && (shelf.getTile(coords[i]).getColor().ordinal() == i ) )
                numOfCorrectTiles++;
        }
        Config config = Config.getInstance();
        final int numOfTiles = numOfCorrectTiles;

        return Arrays.stream(config.getPrivateGoalsScores()).filter(g -> g.correctPosition() == numOfTiles).mapToInt(g -> g.score()).findFirst().getAsInt();
    }

}
