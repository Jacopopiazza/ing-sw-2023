package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.Utilities.Config;
import it.polimi.ingsw.ModelView.GlobalGoalView;

import java.util.*;

/**
 * The GlobalGoal class represents a global goal of the game.
 * Global goals define objectives that players can achieve during the game to increase their score.
 * It implements the {@code Cloneable} interface.
 */
public abstract class GlobalGoal implements Cloneable {
    private Stack<Integer> scores;
    protected final int id;
    protected String description;

    /**
     * Gets instances of global goals based on the number of players.
     *
     * @param people the number of players.
     * @return a list of global goals.
     * @throws InvalidNumberOfPlayersException if the number of players is invalid.
     */
    public static List<GlobalGoal> getInstances(int people) throws InvalidNumberOfPlayersException {
        List<GlobalGoal> goals = new ArrayList<GlobalGoal>();
        goals.add(new Angles(people));
        goals.add(new Shape(people,Config.getInstance().getDiagonalsFromJSON()));
        goals.add(new ColumnsOrRows(people, false, true, 2, Shelf.getRows()));
        goals.add(new ColumnsOrRows(people, false, false, 2, Shelf.getColumns()));
        goals.add(new EightTiles(people));
        goals.add(new ColumnsOrRows(people, true, true, 3, 3));
        goals.add(new ColumnsOrRows(people, true, false, 4, 3));
        goals.add(new GroupOfTiles(people,4,4));
        goals.add(new Square(people));
        goals.add(new Stair(people));
        goals.add(new GroupOfTiles(people,2,6));
        goals.add(new Shape(people,Config.getInstance().getXShapeFromJSON()));
        return goals;
    }

    /**
     * Constructs a GlobalGoal with the specified number of players and ID.
     *
     * @param people the number of players.
     * @param id     the ID of the global goal.
     * @throws InvalidNumberOfPlayersException if the number of players is invalid.
     */
    protected GlobalGoal(int people, int id) throws InvalidNumberOfPlayersException {
        if( ( people < 0 ) || ( people > Config.getInstance().getMaxNumberOfPlayers() ) ) {
            throw new InvalidNumberOfPlayersException();
        }

        // Get a copy of the array, sorted by score so that the smaller rewards go to the bottom of the stack
        Config.GlobalGoalScore[] globalGoalScores = Arrays.stream(Config.getInstance().getUnsortedGlobalGoals()).sorted((g1, g2) -> Integer.compare(g1.score(),g2.score())).toArray(Config.GlobalGoalScore[]::new);

        scores = new Stack<Integer>();
        for( Config.GlobalGoalScore ggp : globalGoalScores ){
            if( people >= ggp.players() )
                scores.push(ggp.score());
        }

        this.id = id;

    }

    /**
     * Gets the view representation of the global goal.
     *
     * @return the view representation of the global goal.
     */
    public GlobalGoalView getView(){
        try {
            return new GlobalGoalView(this.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Checks if the given shelf satisfies the global goal.
     *
     * @param s the shelf to check.
     * @return true if the shelf satisfies the global goal, false otherwise.
     * @throws MissingShelfException if the shelf is missing or null.
     */
    public abstract boolean check(Shelf s) throws MissingShelfException;

    /**
     * Gets the ID of the global goal.
     *
     * @return the ID of the global goal.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the description of the global goal.
     *
     * @return the description of the global goal.
     */
    public String getDescription(){
        return description;
    }

    /**
     * Removes and returns the next score from the stack of scores.
     *
     * @return the next score.
     * @throws EmptyStackException if the stack of scores is empty.
     */
    public int popScore() throws EmptyStackException{
        return scores.pop();
    }

    /**
     * Creates a clone of the global goal.
     *
     * @return a clone of the global goal.
     * @throws CloneNotSupportedException if cloning is not supported.
     */
    @Override
    public GlobalGoal clone() throws CloneNotSupportedException {
        GlobalGoal gg = (GlobalGoal) super.clone();
        gg.scores = (Stack) scores.clone();
        return gg;
    }

}
