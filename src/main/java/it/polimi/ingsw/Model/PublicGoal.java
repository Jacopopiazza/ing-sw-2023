package it.polimi.ingsw.Model;

import java.util.Stack;

public abstract class PublicGoal {

    private Stack<Integer> scores;

    abstract public boolean check(Shelf shelf);

    abstract public int getScore();


}
