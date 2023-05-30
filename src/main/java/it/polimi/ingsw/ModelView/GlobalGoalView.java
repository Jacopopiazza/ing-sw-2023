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
    public String getDescription(){
        switch(id){
            case 1: return "Two groups each containing 4 tiles of " +
                    "the same type in a 2x2 square. The tiles " +
                    "of one square can be different from " +
                    "those of the other square.";
            case 2: return "Two columns each formed by 6 " +
                    "different types of tiles.";
            case 3: return "Four groups each containing at least " +
                    "4 tiles of the same type (not necessarily " +
                    "in the depicted shape). " +
                    "The tiles of one group can be different " +
                    "from those of another group.";
            case 4: return "Six groups each containing at least " +
                    "2 tiles of the same type (not necessarily " +
                    "in the depicted shape). " +
                    "The tiles of one group can be different " +
                    "from those of another group.";
            case 5: return "Three columns each formed by 6 tiles Five tiles of the same type forming an X. " +
                    "of maximum three different types. One " +
                    "column can show the same or a different " +
                    "combination of another column.";
            case 6: return "Two lines each formed by 5 different " +
                    "types of tiles. One line can show the " +
                    "same or a different combination of the " +
                    "other line.";
            case 7: return "Four lines each formed by 5 tiles of " +
                    "maximum three different types. One " +
                    "line can show the same or a different " +
                    "combination of another line.";
            case 8: return "Four tiles of the same type in the four " +
                    "corners of the bookshelf.";
            case 9: return "Eight tiles of the same type. There’s no " +
                    "restriction about the position of these " +
                    "tiles.";
            case 10: return "Five tiles of the same type forming an X.";
            case 11: return "Five tiles of the same type forming a " +
                    "diagonal. ";
            case 12: return "Five columns of increasing or decreasing " +
                    "height. Starting from the first column on " +
                    "the left or on the right, each next column " +
                    "must be made of exactly one more tile. " +
                    "Tiles can be of any type.";
        }
        return "";
    }

}
