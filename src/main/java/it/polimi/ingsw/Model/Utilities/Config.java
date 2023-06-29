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

/**
 * The Config class is responsible for loading and providing configuration data for the game.
 */
@SuppressWarnings("ALL")
public class Config {

    /**
     * Represents the score for a private goal.
     */
    public record PrivateGoalScore(int correctPosition, int score) { }

    /**
     * Represents the score for a global goal.
     */
    public record GlobalGoalScore(int players, int score) { }

    /**
     * Represents the score for a board goal.
     */
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
    private final Integer rmiPort;
    private final Integer socketPort;
    private final List<List<Coordinates>> XShapeFromJSON;
    private final List<List<Coordinates>> DiagonalsFromJSON;
    private final String ipServer;

    /**
     * Private constructor, used for the singleton pattern. This method loads the configuration data from JSON files.
     */
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
        int tempRmiPort, tempSocketPort;
        try{
            tempRmiPort = jsonConfig.get("rmiPort").getAsInt();

            if(!IPAddressValidator.isValidPort(tempRmiPort)){
                tempRmiPort = 1099;
            }

        }catch (NumberFormatException ex){
            tempRmiPort = 1099;
        }

        try{
            tempSocketPort = jsonConfig.get("socketPort").getAsInt();
            if(!IPAddressValidator.isValidPort(tempSocketPort)){
                tempSocketPort = 1234;
            }
        }catch (NumberFormatException ex){
            tempSocketPort = 1234;
        }

        if(tempSocketPort == tempRmiPort){
            tempRmiPort = 1099;
            tempSocketPort = 1234;
        }

        if(!IPAddressValidator.isValidIPAddress(jsonConfig.get("ipServer").getAsString())){
            ipServer = "127.0.0.1";
        }
        else{
            ipServer = jsonConfig.get("ipServer").getAsString();
        }

        rmiPort = tempRmiPort;
        socketPort = tempSocketPort;

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

    /**
     * Returns the scores for private goals.
     *
     * @return an array of PrivateGoalScore objects representing the scores for private goals.
     */
    public PrivateGoalScore[] getPrivateGoalsScores() {
        return privateGoalsScores;
    }

    /**
     * Returns the private goals.
     *
     * @return a list of Coordinates arrays representing the private goals.
     */
    public List<Coordinates[]> getPrivateGoals(){
        return privateGoals;
    }

    /**
     * Returns the game board coordinates for the specified number of players.
     *
     * @param people the number of players in the game.
     * @return a list of Coordinates representing the game board coordinates.
     */
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

    /**
     * Returns the X shape coordinates from JSON.
     *
     * @return a list of Lists of Coordinates representing the X shape coordinates.
     */
    public List<List<Coordinates>> getXShapeFromJSON() {
        return XShapeFromJSON;
    }

    /**
     * Returns the diagonal coordinates from JSON.
     *
     * @return a list of Lists of Coordinates representing the diagonal coordinates.
     */
    public List<List<Coordinates>> getDiagonalsFromJSON() {
        return DiagonalsFromJSON;
    }

    /**
     * Returns the unsorted global goals.
     *
     * @return an array of GlobalGoalScore objects representing the unsorted global goals.
     */
    public GlobalGoalScore[] getUnsortedGlobalGoals() {
        return globalGoals;
    }

    /**
     * Returns the sorted board goals.
     *
     * @return an array of BoardGoalScore objects representing the sorted board goals.
     */
    public BoardGoalScore[] getSortedBoardGoals() {
        return Arrays.stream(boardGoals).sorted((t1, t2) -> t1.tiles() - t2.tiles()).toArray(BoardGoalScore[]::new);
    }

    /**
     * Returns the maximum number of players.
     *
     * @return the maximum number of players.
     */
    public int getMaxNumberOfPlayers() {
        return maxNumberOfPlayers;
    }

    /**
     * Returns the number of global goals.
     *
     * @return the number of global goals.
     */
    public int getNumOfGlobalGoals() {
        return numOfGlobalGoals;
    }

    /**
     * Returns the number of rows in the shelf.
     *
     * @return the number of rows in the shelf.
     */
    public int getShelfRows() {
        return shelfRows;
    }

    /**
     * Returns the number of columns in the shelf.
     *
     * @return the number of columns in the shelf.
     */
    public int getShelfColumns() {
        return shelfColumns;
    }

    /**
     * Returns the number of tiles per color.
     *
     * @return the number of tiles per color.
     */
    public int getNumOfTilesPerColor() {
        return numOfTilesPerColor;
    }

    /**
     * Returns the singleton instance of Config.
     *
     * @return the Config instance.
     */
    public static synchronized Config getInstance(){
        if( instance == null ) instance = new Config();
        return instance;
    }

    /**
     * Returns the IP address of the server.
     *
     * @return the IP address of the server
     */
    public String getIpServer() {
        return ipServer;
    }

    /**
     * Returns the RMI port of the server.
     *
     * @return the RMI port of the server
     */
    public Integer getRmiPort() {
        return rmiPort;
    }

    /**
     * Returns the socket port of the server.
     *
     * @return the socket port of the server
     */
    public Integer getSocketPort() {
        return socketPort;
    }
}
