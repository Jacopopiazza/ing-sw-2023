package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.GlobalGoals.*;

import java.util.ArrayList;
import java.util.List;
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

    public abstract boolean check(Shelf s) throws ColumnOutOfBoundsException,MissingShelfException;

    public int popScore() throws EmptyStackException{
        return scores.pop();
    }

    public static List<GlobalGoal> getOneForEachChild(int nofpeople) throws InvalidNumberOfPlayersException {
        List<GlobalGoal> goals = new ArrayList<GlobalGoal>();
        goals.add( new Angles(nofpeople) );
        goals.add( new Diagonal(nofpeople) );
        goals.add( new DifferentColumns(nofpeople) );
        goals.add( new DifferentLines(nofpeople) );
        goals.add( new EightTiles(nofpeople) );
        goals.add( new EqualColumns(nofpeople) );
        goals.add( new EqualLines(nofpeople) );
        goals.add( new FourTiles(nofpeople) );
        goals.add( new Square(nofpeople) );
        goals.add( new Stair(nofpeople) );
        goals.add( new TwoTiles(nofpeople) );
        goals.add( new XShape(nofpeople) );
        return goals;
    }
}
