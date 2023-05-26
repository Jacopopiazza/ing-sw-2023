package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.Config;

import java.util.*;

public final class PrivateGoal {
    private Coordinates[] coords;

    private PrivateGoal(Coordinates[] coords) {
        this.coords = coords.clone();
    }

    public Coordinates[] getCoordinates() {
        Coordinates[] temp = new Coordinates[coords.length];
        for(int i =0; i< coords.length;i++) temp[i] = coords[i].clone();
        return temp;
    }

    public static PrivateGoal[] getPrivateGoals(int people) throws InvalidNumberOfPlayersException {
        if( ( people <= 0 ) || ( people > Config.getInstance().getMaxNumberOfPlayers() ) ) {
            throw new InvalidNumberOfPlayersException();
        }

        PrivateGoal[] retValue = new PrivateGoal[people];

        List<Coordinates[]> allPrivateGoals = Config.getInstance().getPrivateGoals();
        Collections.shuffle(allPrivateGoals);

        for( int i = 0; i < people; i++ )
            retValue[i] = new PrivateGoal(allPrivateGoals.get(i));

        return retValue;
    }


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
