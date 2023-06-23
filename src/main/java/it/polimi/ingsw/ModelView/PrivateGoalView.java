package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.PrivateGoal;

import java.io.Serializable;

/**
 * The PrivateGoalView class represents a view of a private goal in the game.
 * It provides a snapshot of the private goal's attributes in a serializable format.
 */

public class PrivateGoalView implements Serializable {

    private final int id;
    private final Coordinates[] coords;

    /**
     * Constructs a new PrivateGoalView object based on the given PrivateGoal object.
     *
     * @param goal the PrivateGoal object to create the view from
     */

    public PrivateGoalView(PrivateGoal goal){
        coords = goal.getCoordinates();
        id = goal.getId();
    }

    /**
     * Retrieves the array of coordinates associated with the private goal.
     *
     * @return an array of Coordinates representing the associated coordinates
     */

    public Coordinates[] getCoordinates(){
        return coords;
    }

    /**
     * Retrieves the ID of the private goal.
     *
     * @return the ID of the private goal
     */

    public int getId(){
        return id;
    }
}
