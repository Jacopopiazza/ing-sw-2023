package it.polimi.ingsw.Model;

import com.google.gson.Gson;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.JSONModels.JSONGameBoard;
import it.polimi.ingsw.Model.Utilities.Config;
import it.polimi.ingsw.Model.Utilities.IslandCounter;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public class GameBoard {

    private Map<Coordinates,Tile> board;

    private GameBoard(){ }

    public static GameBoard getGameBoard(int people) throws InvalidNumberOfPlayersException {
        if(people > Config.getInstance().getMaxNumberOfPlayers()){
            throw new InvalidNumberOfPlayersException();
        }

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(GameBoard.class.getResourceAsStream("/GameBoard.json"));
        JSONGameBoard[] gameBoardConfigsFromFile = gson.fromJson(reader, JSONGameBoard[].class);

        GameBoard gb = new GameBoard();

        // for each gameBoardConfig read from the file
        // add its cells to the HashMap
        for(int i = 0; i<gameBoardConfigsFromFile.length; i++){

            // Once added the cells for the requested amount of people stop
            if(gameBoardConfigsFromFile[i].people() > people){
                break;
            }

            // Add all the cells for the current config in the HashMap
            for (Coordinates c : gameBoardConfigsFromFile[i].cells()) {
                gb.board.put(c, null);
            }

        }


        return gb;

    }

    public Set<Coordinates> getCoords(){

        return Collections.unmodifiableSet(board.keySet());
    }

    public Tile getTile(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
        if(!board.containsKey(c)){
            throw new InvalidCoordinatesForCurrentGameException();
        }

        return board.get(c) != null ? (Tile)board.get(c).clone() : null;
    }

    public void setTile(Coordinates c, Tile t) throws InvalidCoordinatesForCurrentGameException{
        if(!board.containsKey(c)){
            throw new InvalidCoordinatesForCurrentGameException();
        }

        board.put(c,(Tile)t.clone());
    }

    public static int checkBoardGoal(Shelf s){

        List<Integer> results = IslandCounter.countIslands(s).stream().filter(num -> num >= 3).collect(Collectors.toList());

        int totalScore = 0;

        for (Integer islandOf : results){
            if(islandOf >= 6){
                totalScore += 8;
            } else if (islandOf == 5) {
                totalScore += 5;
            } else if(islandOf == 4){
                totalScore += 3;
            }
            totalScore += 2;
        }

        return totalScore;
    }

    public boolean isPickable(Coordinates c) throws InvalidCoordinatesForCurrentGameException {

        if(!board.containsKey(c)) throw new InvalidCoordinatesForCurrentGameException();

        int[] adjacentX = new int[]{-1,0,0,+1};
        int[] adjacentY = new int[]{0,+1,-1,0};

        for(int i=0;i<adjacentX.length;i++){
            if(!board.containsKey(new Coordinates(c.getX()+adjacentX[i],c.getY()+adjacentY[i])) ||
                board.get(new Coordinates(c.getX()+adjacentX[i],c.getY()+adjacentY[i])) == null){
                return true;
            }
        }


        return false;
    }
}
