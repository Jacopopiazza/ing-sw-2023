package it.polimi.ingsw.Model.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.io.Reader;

public class Config {

    public record PrivateGoalScore(int correctPosition, int score) { }
    public record GlobalGoalScore(int players, int score) { }
    private static Config instance;
    private final int maxNumberOfPlayers;
    private final int shelfRows;
    private final int shelfColumns;
    private final int numOfTilesPerColor;
    private final PrivateGoalScore[] privateGoals;
    private final GlobalGoalScore[] globalGoals;

    private Config(){
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/config.json"));
        JsonObject jsonConfig = gson.fromJson(reader, JsonObject.class);
        privateGoals = gson.fromJson(jsonConfig.get("privateGoals"), PrivateGoalScore[].class);
        globalGoals = gson.fromJson(jsonConfig.get("globalGoals"), GlobalGoalScore[].class);
        shelfRows = jsonConfig.get("shelfRows").getAsInt();
        shelfColumns = jsonConfig.get("shelfColumns").getAsInt();
        numOfTilesPerColor = jsonConfig.get("numOfTilesPerColor").getAsInt();
        maxNumberOfPlayers = jsonConfig.get("maxNumberOfPlayers").getAsInt();
    }

    public PrivateGoalScore[] getPrivateGoals() {
        return privateGoals;
    }

    public GlobalGoalScore[] getUnsortedGlobalGoals() {
        return globalGoals;
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public int getShelfRows() {
        return shelfRows;
    }

    public int getShelfColumns() {
        return shelfColumns;
    }

    public int getNumOfTilesPerColor() {
        return numOfTilesPerColor;
    }

    public static synchronized Config getInstance(){
        if( instance == null ) instance = new Config();
        return instance;
    }
}
