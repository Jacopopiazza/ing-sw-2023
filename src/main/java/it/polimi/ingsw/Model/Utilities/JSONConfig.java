package it.polimi.ingsw.Model.Utilities;

public class JSONConfig{

    public record PrivateGoalPoint(int correctPosition, int points) {

    }

    private PrivateGoalPoint[] privateGoals;

    public PrivateGoalPoint[] getPrivateGoals() {
        return privateGoals;
    }
}