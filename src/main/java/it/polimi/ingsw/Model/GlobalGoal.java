package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;

import java.util.Stack;

public abstract class GlobalGoal {
    private Stack<Integer> scores;

    public GlobalGoal(int people) throws InvalidNumberOfPlayersException{

        if(people < 0 || people > Game.maxNumberOfPlayers){
            throw new InvalidNumberOfPlayersException();
        }

        scores = new Stack<Integer>();

        if(people >= 4) { scores.push(2); }

        scores.push(4);

        if (people >= 3) { scores.push(6); }

        scores.push(8);

    }

    public abstract boolean check(Shelf s) throws ColumnOutOfBoundsException;

    public int popScore() throws EmptyStackException{
        return scores.pop();
    }
}
