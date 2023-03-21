package it.polimi.ingsw.Model;

import com.google.gson.Gson;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.IslandCounter;
import org.example.App;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

public class GameBoard {

    private static class GameBoardFromJson{
        public int getPeople() {
            return people;
        }

        public Coordinates[] getCells() {
            return cells;
        }

        private int people;

        private Coordinates[] cells;

        public GameBoardFromJson(int people, Coordinates[] cells){
            this.people = people;
            this.cells = cells.clone();
        }

    }
    private Map<Coordinates,Tile> board;

    private GameBoard(){

    }

    public static GameBoard getGameBoard(int people) throws InvalidNumberOfPlayersException {
        if(people > Game.maxNumberOfPlayers){
            throw new InvalidNumberOfPlayersException();
        }

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(GameBoard.class.getResourceAsStream("/GameBoard.json"));
        GameBoardFromJson[] gameBoardConfigsFromFile = gson.fromJson(reader, GameBoardFromJson[].class);

        GameBoard gb = new GameBoard();


        for(int i = 0; i<gameBoardConfigsFromFile.length; i++){

            if(gameBoardConfigsFromFile[i].getPeople() > people){
                break;
            }

            for (Coordinates c : gameBoardConfigsFromFile[i].getCells()) {
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

        return (Tile)board.get(c).clone();
    }

    public void setTile(Coordinates c, Tile t) throws InvalidCoordinatesForCurrentGameException{
        if(!board.containsKey(c)){
            throw new InvalidCoordinatesForCurrentGameException();
        }

        board.put(c,(Tile)t.clone());
    }

    public Tile pickTile(Coordinates c) throws InvalidCoordinatesForCurrentGameException{
        if(!board.containsKey(c)){
            throw new InvalidCoordinatesForCurrentGameException();
        }

        Tile t = board.get(c);
        board.put(c,null);
        return t;
    }

    public int checkBoardGoal(Shelf s){

        List<Integer> results = IslandCounter.countIslands(s)
                .stream().filter(num -> num >= 3).collect(Collectors.toList());

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
}
