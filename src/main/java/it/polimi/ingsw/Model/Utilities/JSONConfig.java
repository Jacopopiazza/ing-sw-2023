package it.polimi.ingsw.Model.Utilities;

public class JSONConfig{

    public record PrivateGoalPoint(int correctPosition, int points) {

    }

    public record GlobalGoalPoint(int players, int points, boolean alwaysPresent) { }


    private PrivateGoalPoint[] privateGoals;

    public GlobalGoalPoint[] getGlobalGoals() {
        return globalGoals;
    }

    private GlobalGoalPoint[] globalGoals;

    public PrivateGoalPoint[] getPrivateGoals() {
        return privateGoals;
    }
}