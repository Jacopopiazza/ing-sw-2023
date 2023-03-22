package it.polimi.ingsw.Model.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.io.Reader;

public class Config {

    public record PrivateGoalPoint(int correctPosition, int score) { }
    public record GlobalGoalPoint(int players, int score) { }

    private static Config instance;
    private int maxNumberOfPlayers;
    private int shelfRows;
    private int shelfColumns;

    private int numOfTilesPerColor;

    private PrivateGoalPoint[] privateGoals;
    private GlobalGoalPoint[] globalGoals;

    private Config(int maxNumberOfPlayers, int shelfRows, int shelfColumns, int numOfTilesPerColor, PrivateGoalPoint[] privateGoals, GlobalGoalPoint[] globalGoals){
        this.maxNumberOfPlayers = maxNumberOfPlayers;
        this.privateGoals = privateGoals;
        this.globalGoals = globalGoals;
        this.shelfRows = shelfRows;
        this.shelfColumns = shelfColumns;
        this.numOfTilesPerColor = numOfTilesPerColor;
    }

    public PrivateGoalPoint[] getPrivateGoals() {
        return privateGoals;
    }

    public GlobalGoalPoint[] getGlobalGoals() {
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
        if(instance == null){

            Gson gson = new Gson();
            Reader reader = new InputStreamReader(Config.class.getResourceAsStream("/config.json"));

            JsonObject jsonConfig = gson.fromJson(reader, JsonObject.class);

            PrivateGoalPoint[] privateGoals = gson.fromJson(jsonConfig.get("privateGoals"), PrivateGoalPoint[].class);
            GlobalGoalPoint[] globalGoals = gson.fromJson(jsonConfig.get("globalGoals"), GlobalGoalPoint[].class);

            instance = new Config(jsonConfig.get("maxNumberOfPlayers").getAsInt(),jsonConfig.get("shelfRows").getAsInt(),
                    jsonConfig.get("shelfColumns").getAsInt(),jsonConfig.get("numOfTilesPerColor").getAsInt(),privateGoals,globalGoals);
        }
        return instance;
    }
}
