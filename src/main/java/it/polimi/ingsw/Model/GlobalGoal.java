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

    public static List<GlobalGoal> getOneForEachChild(int people) throws InvalidNumberOfPlayersException {
        List<GlobalGoal> goals = new ArrayList<GlobalGoal>();
        goals.add( new Angles(people) );
        goals.add( new Diagonal(people) );
        goals.add( new DifferentColumns(people) );
        goals.add( new DifferentLines(people) );
        goals.add( new EightTiles(people) );
        goals.add( new EqualColumns(people) );
        goals.add( new EqualLines(people) );
        goals.add( new FourTiles(people) );
        goals.add( new Square(people) );
        goals.add( new Stair(people) );
        goals.add( new TwoTiles(people) );
        goals.add( new XShape(people) );
        return goals;
    }
}
