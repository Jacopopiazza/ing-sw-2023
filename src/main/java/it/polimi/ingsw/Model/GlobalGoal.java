package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Exceptions.EmptyStackException;
import it.polimi.ingsw.Model.GlobalGoals.*;
import it.polimi.ingsw.Model.Utilities.Config;

import java.util.*;

public abstract class GlobalGoal {
    private Stack<Integer> scores;

    public GlobalGoal(int people) throws InvalidNumberOfPlayersException {

        if (people < 0 || people > Config.getInstance().getMaxNumberOfPlayers()) {
            throw new InvalidNumberOfPlayersException();
        }

        scores = new Stack<Integer>();

        Config config = Config.getInstance();

        // Get a copy of the array sorted by score so the
        // smaller rewards goes to the bottom of the stack
        Config.GlobalGoalPoint[] globalGoalPoints = Arrays.stream(config.getGlobalGoals()).sorted((g1, g2) -> Integer.compare(g1.score(),g2.score()))
                .toArray(Config.GlobalGoalPoint[]::new);

        for(Config.GlobalGoalPoint ggp : globalGoalPoints){
            if(people >= ggp.players() ){
                scores.push(ggp.score());
            }
        }

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
