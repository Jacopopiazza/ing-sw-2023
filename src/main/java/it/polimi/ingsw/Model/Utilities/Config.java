package it.polimi.ingsw.Model.Utilities;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.Model.Coordinates;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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
    private final int numOfGlobalGoals;
    private final JsonArray gameBoardJsonCoordinatesInfo;
    private final List<Coordinates[]> privateGoals;
    private final PrivateGoalScore[] privateGoalsScores;
    private final GlobalGoalScore[] globalGoals;
    private final BoardGoalScore[] boardGoals;

    private final List<List<Coordinates>> XShapeFromJSON;
    private final List<List<Coordinates>> DiagonalsFromJSON;

    private Config(){
        Gson gson = new Gson();
        Reader readerConfig = new InputStreamReader(this.getClass().getResourceAsStream("/Config.json"));
        JsonObject jsonConfig = gson.fromJson(readerConfig, JsonObject.class);
        privateGoalsScores = gson.fromJson(jsonConfig.get("privateGoalsScores"), PrivateGoalScore[].class);
        globalGoals = gson.fromJson(jsonConfig.get("globalGoals"), GlobalGoalScore[].class);
        boardGoals = gson.fromJson(jsonConfig.get("boardGoals"), BoardGoalScore[].class);
        shelfRows = jsonConfig.get("shelfRows").getAsInt();
        shelfColumns = jsonConfig.get("shelfColumns").getAsInt();
        numOfTilesPerColor = jsonConfig.get("numOfTilesPerColor").getAsInt();
        maxNumberOfPlayers = jsonConfig.get("maxNumberOfPlayers").getAsInt();
        numOfGlobalGoals = jsonConfig.get("numOfGlobalGoals").getAsInt();

        Reader readerPG = new InputStreamReader(this.getClass().getResourceAsStream("/PrivateGoals.json"));
        JsonArray baseArray = gson.fromJson(readerPG, JsonArray.class);
        privateGoals = new ArrayList<Coordinates[]>();
        JsonArray prvGoal;
        for( JsonElement jsonPrivateGoal : baseArray ){
            prvGoal = jsonPrivateGoal.getAsJsonArray();
            Coordinates[] coords = gson.fromJson(prvGoal, Coordinates[].class);
            privateGoals.add(coords);
        }

        Reader readerGB = new InputStreamReader(this.getClass().getResourceAsStream("/GameBoard.json"));
        gameBoardJsonCoordinatesInfo = gson.fromJson(readerGB, JsonArray.class);

        Reader readerGG = new InputStreamReader(this.getClass().getResourceAsStream("/GlobalGoals.json"));
        XShapeFromJSON = new ArrayList<>();
        JsonObject globalGoalsJson = gson.fromJson(readerGG, JsonObject.class);
        XShapeFromJSON.add(new ArrayList<>());

        JsonArray XShapeCoords = globalGoalsJson.get("XShape").getAsJsonArray();
        for( JsonElement coords : XShapeCoords ){
            XShapeFromJSON.get(0).add(new Coordinates(coords.getAsJsonObject().get("r").getAsInt(), coords.getAsJsonObject().get("c").getAsInt()));
        }

        DiagonalsFromJSON = new ArrayList<>();
        DiagonalsFromJSON.add(new ArrayList<>());
        DiagonalsFromJSON.add(new ArrayList<>());

        JsonArray FirstDiagonalCoords = globalGoalsJson.get("FirstDiagonal").getAsJsonArray();
        for( JsonElement coords : FirstDiagonalCoords ){
            DiagonalsFromJSON.get(0).add(new Coordinates(coords.getAsJsonObject().get("r").getAsInt(), coords.getAsJsonObject().get("c").getAsInt()));
        }

        JsonArray SecondDiagonalCoords = globalGoalsJson.get("SecondDiagonal").getAsJsonArray();
        for( JsonElement coords : SecondDiagonalCoords ){
            DiagonalsFromJSON.get(1).add(new Coordinates(coords.getAsJsonObject().get("r").getAsInt(), coords.getAsJsonObject().get("c").getAsInt()));
        }

    }

    public PrivateGoalScore[] getPrivateGoalsScores() {
        return privateGoalsScores;
    }

    public List<Coordinates[]> getPrivateGoals(){
        return privateGoals;
    }

    public List<Coordinates> getGameBoardCoordinates(int people){
        int currPeople;
        List<Coordinates> gameBoardCoordinates = new ArrayList<>();
        for( JsonElement elem : gameBoardJsonCoordinatesInfo ){
            JsonObject obj = (JsonObject) elem.getAsJsonObject();
            currPeople = obj.get("people").getAsInt();
            if( currPeople > people ) continue;
            JsonArray jsonCells = obj.get("cells").getAsJsonArray();
            int r, c;
            for( JsonElement jsonCell : jsonCells ){
                r = jsonCell.getAsJsonObject().get("ROW").getAsInt();
                c = jsonCell.getAsJsonObject().get("COL").getAsInt();
                gameBoardCoordinates.add(new Coordinates(r,c));
            }
        }
        return gameBoardCoordinates;
    }

    public List<List<Coordinates>> getXShapeFromJSON() {
        return XShapeFromJSON;
    }

    public List<List<Coordinates>> getDiagonalsFromJSON() {
        return DiagonalsFromJSON;
    }

    public GlobalGoalScore[] getUnsortedGlobalGoals() {
        return globalGoals;
    }

    public BoardGoalScore[] getSortedBoardGoals() {
        //return (BoardGoalScore[]) Arrays.stream(boardGoals).sorted((t1,t2)->t1.tiles()-t2.tiles()).collect(Collectors.toList()).toArray();
        List<BoardGoalScore> l = Arrays.stream(boardGoals).sorted((t1, t2)->t1.tiles()-t2.tiles()).collect(Collectors.toList());
        return l.toArray(new BoardGoalScore[l.size()]);
    }

    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    public int getNumOfGlobalGoals() {
        return numOfGlobalGoals;
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
