package it.polimi.ingsw.Model;

import com.google.gson.Gson;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Exceptions.EmptyStackException;
import it.polimi.ingsw.Model.GlobalGoals.*;
import it.polimi.ingsw.Model.Utilities.JSONConfig;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public abstract class GlobalGoal {
    private Stack<Integer> scores;

    public GlobalGoal(int people) throws InvalidNumberOfPlayersException {

        if (people < 0 || people > Game.maxNumberOfPlayers) {
            throw new InvalidNumberOfPlayersException();
        }

        scores = new Stack<Integer>();

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(GlobalGoal.class.getResourceAsStream("/config.json"));
        JSONConfig config = gson.fromJson(reader, JSONConfig.class);

        // Get a copy of the array sorted by points so the
        // smaller rewards goes to the bottom of the stack
        JSONConfig.GlobalGoalPoint[] globalGoalPoints = Arrays.stream(config.getGlobalGoals()).sorted((g1, g2) -> Integer.compare(g1.points(),g2.points()))
                .toArray(JSONConfig.GlobalGoalPoint[]::new);

        for(JSONConfig.GlobalGoalPoint ggp : globalGoalPoints){
            if(ggp.alwaysPresent() || people >= ggp.players() ){
                scores.push(ggp.points());
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
