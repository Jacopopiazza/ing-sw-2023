package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;

import java.util.Stack;

public abstract class GlobalGoal {
    private Stack<Integer> scores;

    public GlobalGoal(int persone) throws InvalidNumberOfPlayersException{

        if(persone < 0 || persone > Game.maxNumberOfPlayers){
            throw new InvalidNumberOfPlayersException();
        }

        scores = new Stack<Integer>();

        if(persone >= 4) { scores.push(2); }

        scores.push(4);

        if (persone >= 3) { scores.push(6); }

        scores.push(8);

    }

    public abstract boolean check(Shelf s);

    public int popScore() throws EmptyStackException{
        return scores.pop();
    }
}
