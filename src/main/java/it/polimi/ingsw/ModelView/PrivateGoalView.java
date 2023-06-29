package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.PrivateGoal;

import java.io.Serializable;

/**
 * The {@code PrivateGoalView} class represents the immutable version of the {@link it.polimi.ingsw.Model.PrivateGoal}.
 * It provides a snapshot of the player's private goal in a serializable format.
 */
public class PrivateGoalView implements Serializable {

    /**
     * Id of the PrivateGoal.
     */
    private final int id;

    /**
     * Array of the Coordinates in the Shelf where the Tiles should be placed.
     */
    private final Coordinates[] coords;

    /**
     * Constructs a new {@code PrivateGoalView} object based on the given {@code PrivateGoal} object.
     *
     * @param goal the {@link PrivateGoal} object to create the view from
     */

    public PrivateGoalView(PrivateGoal goal){
        coords = goal.getCoordinates();
        id = goal.getId();
    }

    /**
     * Retrieves the array of {@code Coordinates} associated with the private goal.
     *
     * @return an array of {@link Coordinates} representing the associated coordinates
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
