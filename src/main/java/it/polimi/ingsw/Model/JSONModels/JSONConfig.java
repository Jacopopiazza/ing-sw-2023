package it.polimi.ingsw.Model.JSONModels;

public class JSONConfig{

    public record PrivateGoalPoint(int correctPosition, int points) { }
    public record GlobalGoalPoint(int players, int points, boolean alwaysPresent) { }

    private PrivateGoalPoint[] privateGoals;
    private GlobalGoalPoint[] globalGoals;
    private int maxNumberOfPlayers;


    public GlobalGoalPoint[] getGlobalGoals() { return globalGoals; }

    public int getMaxNumberOfPlayers() { return maxNumberOfPlayers; }

    public PrivateGoalPoint[] getPrivateGoals() {
        return privateGoals;
    }
}