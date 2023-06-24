package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.GlobalGoals.GlobalGoal;

import java.io.Serializable;
import java.util.EmptyStackException;

/**
 * The GlobalGoalView class represents the view of a global goal in the model-view architecture.
 * It provides a simplified representation of the global goal state for the client-side view.
 */
public class GlobalGoalView implements Serializable {

    private final int score;
    private final int id;
    private final String description;

    /**
     * Constructs a GlobalGoalView object based on the provided GlobalGoal.
     *
     * @param gg the GlobalGoal object to include in the view
     */
    public GlobalGoalView(GlobalGoal gg){
        int temp;
        try {
            temp = gg.popScore();
        } catch (EmptyStackException e) {
            temp = 0;
        }
        score = temp;

        id = gg.getId();
        description = gg.getDescription();
    }

    /**
     * Returns the current score of the global goal.
     *
     * @return the current score
     */
    public int getCurrentScore(){
        return score;
    }

    /**
     * Returns the ID of the global goal.
     *
     * @return the ID
     */
    public int getId(){
        return id;
    }

    /**
     * Returns the description of the global goal.
     *
     * @return the description
     */
    public String getDescription(){return description;}

}
