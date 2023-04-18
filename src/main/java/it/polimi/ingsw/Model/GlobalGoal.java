package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Exceptions.EmptyStackException;
import it.polimi.ingsw.Model.GlobalGoals.*;
import it.polimi.ingsw.Model.Utilities.Config;

import java.util.*;

public abstract class GlobalGoal implements Cloneable {
    private Stack<Integer> scores;

    public GlobalGoal(int people) throws InvalidNumberOfPlayersException {
        if ( ( people < 0 ) || ( people > Config.getInstance().getMaxNumberOfPlayers() ) ) {
            throw new InvalidNumberOfPlayersException();
        }

        Config config = Config.getInstance();

        // Get a copy of the array, sorted by score so that the smaller rewards go to the bottom of the stack
        Config.GlobalGoalScore[] globalGoalScores = Arrays.stream(config.getUnsortedGlobalGoals()).sorted((g1, g2) -> Integer.compare(g1.score(),g2.score())).toArray(Config.GlobalGoalScore[]::new);

        scores = new Stack<Integer>();

        for( Config.GlobalGoalScore ggp : globalGoalScores ){
            if( people >= ggp.players() ) scores.push(ggp.score());
        }

    }

    public abstract boolean check(Shelf s) throws ColumnOutOfBoundsException,MissingShelfException;

    public int popScore() throws EmptyStackException{
        return scores.pop();
    }

    @Override
    public GlobalGoal clone() throws CloneNotSupportedException {
        GlobalGoal gg = (GlobalGoal) super.clone();
        gg.scores = (Stack) scores.clone();
        return gg;
    }

    public static List<GlobalGoal> getOneForEachChild(int people) throws InvalidNumberOfPlayersException {
        List<GlobalGoal> goals = new ArrayList<GlobalGoal>();
        goals.add(new Angles(people));

        List<List<Coordinates>> diagonal= new ArrayList<List<Coordinates>>();
        List<Coordinates> temp = new ArrayList<Coordinates>();
        for(int i=0;i<5;i++) temp.add(new Coordinates(i,i));
        diagonal.add(temp);
        temp = new ArrayList<Coordinates>();
        for(int i=0;i<5;i++) temp.add(new Coordinates(4-i,i));
        diagonal.add(temp);

        goals.add(new Shape(people,diagonal));
        goals.add(new Columns(people, false, 2, Shelf.getRows()));
        goals.add(new Rows(people, false, 2, Shelf.getColumns()));
        goals.add(new EightTiles(people));
        goals.add(new Columns(people, true, 3, 3));
        goals.add(new Rows(people, true, 4, 3));
        goals.add(new GroupOfTiles(people,4,4));
        goals.add(new Square(people));
        goals.add(new Stair(people));
        goals.add(new GroupOfTiles(people,2,6));

        List<List<Coordinates>> xShape= new ArrayList<List<Coordinates>>();
        temp = new ArrayList<Coordinates>();
        for(int i=0;i<3;i++) temp.add(new Coordinates(i,i));
        temp.add(new Coordinates(0,2));
        temp.add(new Coordinates(2,0));
        diagonal.add(temp);

        goals.add(new Shape(people,xShape));
        return goals;
    }

}
