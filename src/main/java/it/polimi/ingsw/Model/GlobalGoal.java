package it.polimi.ingsw.Model.*;

import java.util.Stack;

public abstract class GlobalGoal {
    private Stack<Integer> scores;

    public GlobalGoal(int persone) throws InvalidNumberOfPlayersException{

        if(persone < 0 || persone >= Game.maxNumberOfPlayers){
            throw new InvalidNumberOfPlayersException();
        }

        scores = new Stack<Integer>();

        if(persone >= 4) { score.push(2); }

        score.push(4);

        if (persone >= 3) { score.push(6); }

        score.push(8);

    }

    public abstract boolean check(Shelf s);

    public int popScore() throws EmptyStackException{
        return scores.pop();
    }
}
