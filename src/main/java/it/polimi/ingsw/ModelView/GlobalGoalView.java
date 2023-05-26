package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.GlobalGoals.GlobalGoal;

import java.io.Serializable;
import java.util.EmptyStackException;
import java.util.Stack;

public class GlobalGoalView implements Serializable {

    private final Stack<Integer> scores;
    private final String name;

    public GlobalGoalView(GlobalGoal gg){
        scores = new Stack<>();
        Stack<Integer> temp = new Stack<>();

        try {
            while(true) temp.push(gg.popScore());
        } catch (EmptyStackException e) {
        }

        try {
            while(true) scores.push(temp.pop());
        } catch (EmptyStackException e) {
        }

        name = gg.getName();

    }

}
