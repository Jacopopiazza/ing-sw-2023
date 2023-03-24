package it.polimi.ingsw.Model.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Config {

    public record PrivateGoalScore(int correctPosition, int score) { }
    public record GlobalGoalScore(int players, int score) { }
    public record BoardGoalScore(int tiles, int score) { }
    private static Config instance;
    private final int maxNumberOfPlayers;
    private final int shelfRows;
    private final int shelfColumns;
    private final int numOfTilesPerColor;
    private final PrivateGoalScore[] privateGoals;
    private final GlobalGoalScore[] globalGoals;
    private final BoardGoalScore[] boardGoals;

    private Config(){
        Gson gson = new Gson();
        Reader reader = new InputStreamReader(this.getClass().getResourceAsStream("/config.json"));
        JsonObject jsonConfig = gson.fromJson(reader, JsonObject.class);
        privateGoals = gson.fromJson(jsonConfig.get("privateGoals"), PrivateGoalScore[].class);
        globalGoals = gson.fromJson(jsonConfig.get("globalGoals"), GlobalGoalScore[].class);
        boardGoals = gson.fromJson(jsonConfig.get("boardGoals"), BoardGoalScore[].class);
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

    public BoardGoalScore[] getSortedBoardGoals() {
        return (BoardGoalScore[]) Arrays.stream(boardGoals).sorted((t1,t2)->t1.tiles()-t2.tiles()).collect(Collectors.toList()).toArray();
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
