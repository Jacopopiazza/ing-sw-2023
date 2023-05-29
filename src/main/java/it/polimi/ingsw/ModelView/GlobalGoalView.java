package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.GlobalGoals.GlobalGoal;

import java.io.Serializable;
import java.util.EmptyStackException;

public class GlobalGoalView implements Serializable {

    private final int score;
    private final int id;

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

    public int getCurrentScore(){
        return score;
    }

    public int getId(){
        return id;
    }

}
