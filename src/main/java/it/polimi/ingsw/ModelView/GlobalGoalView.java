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
            case 1: return "Two groups each containing 4 tiles of\n" +
                    "the same type in a 2x2 square. The tiles\n" +
                    "of one square can be different from\n" +
                    "those of the other square.";
            case 2: return "Two columns each formed by 6\n" +
                    "different types of tiles.";
            case 3: return "Four groups each containing at least\n" +
                    "4 tiles of the same type (not necessarily\n" +
                    "in the depicted shape).\n" +
                    "The tiles of one group can be different\n" +
                    "from those of another group.";
            case 4: return "Six groups each containing at least\n" +
                    "2 tiles of the same type (not necessarily\n" +
                    "in the depicted shape).\n" +
                    "The tiles of one group can be different\n" +
                    "from those of another group.";
            case 5: return "Three columns each formed by 6 tiles Five tiles of the same type forming an X.\n" +
                    "of maximum three different types. One\n" +
                    "column can show the same or a different\n" +
                    "combination of another column.";
            case 6: return "Two lines each formed by 5 different\n" +
                    "types of tiles. One line can show the\n" +
                    "same or a different combination of the\n" +
                    "other line.";
            case 7: return "Four lines each formed by 5 tiles of\n" +
                    "maximum three different types. One\n" +
                    "line can show the same or a different\n" +
                    "combination of another line.";
            case 8: return "Four tiles of the same type in the four\n" +
                    "corners of the bookshelf.";
            case 9: return "Eight tiles of the same type. There’s no\n" +
                    "restriction about the position of these\n" +
                    "tiles.";
            case 10: return "Five tiles of the same type forming an X.";
            case 11: return "Five tiles of the same type forming a\n" +
                    "diagonal. ";
            case 12: return "Five columns of increasing or decreasing\n" +
                    "height. Starting from the first column on\n" +
                    "the left or on the right, each next column\n" +
                    "must be made of exactly one more tile.\n" +
                    "Tiles can be of any type.";
        }
        return "";
    }

}
