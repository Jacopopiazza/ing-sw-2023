package it.polimi.ingsw.Model;

import java.util.Stack;

public abstract class PublicGoal {
    private Stack<Integer> scores;

    public abstract boolean check(Shelf s);

    public int popScore(){

    }
}
