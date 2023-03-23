package it.polimi.ingsw.Model;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.Utilities.CoordinatesParser;
import it.polimi.ingsw.Model.Utilities.Config;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

public class GameBoard {

    private Map<Coordinates,Tile> board;

    private GameBoard(){ }

    public static GameBoard getGameBoard(int people) throws InvalidNumberOfPlayersException {
        if(people > Config.getInstance().getMaxNumberOfPlayers()){
            throw new InvalidNumberOfPlayersException();
        }

        // Parse json file and create an instance
        GameBoard gb = new GameBoard();

        Gson gson = new Gson();
        Reader reader = new InputStreamReader(GameBoard.class.getResourceAsStream("/GameBoard.json"));

        JsonArray array = gson.fromJson(reader, JsonArray.class);
        for (JsonElement elem : array){
            JsonObject obj = elem.getAsJsonObject();

            int peopleOfCurrentConfig = obj.get("people").getAsInt();

            if(peopleOfCurrentConfig > people){
                continue;
            }

            JsonArray jsonCells = obj.get("cells").getAsJsonArray();

            for (JsonElement jsonCell : jsonCells) {
                Coordinates c = CoordinatesParser.coordinatesParser(jsonCell);
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

    public static int checkBoardGoal(Shelf s) throws MissingShelfException{
        if(s==null) throw new MissingShelfException();
        int r=s.getRows();
        int c=s.getColumns();
        boolean[][] checked= new boolean[r][c];
        for(int i=0;i<r;i++){
            for(int j=0;j<c;j++){
                checked[i][j]=false;
            }
        }
        int totalScore = 0;

        for(int i=0;i<r;i++){
            for(int j=0;j<c;j++){
                if(checked[i][j]==false){
                    int currentGroup=checkFromThisTile(s,new Coordinates(i,j),checked);
                    if(currentGroup >= 6){
                        totalScore += 8;
                    } else if (currentGroup == 5) {
                        totalScore += 5;
                    } else if(currentGroup == 4){
                        totalScore += 3;
                    } else if(currentGroup == 3){
                        totalScore += 2;
                    }

                }
            }
        }

        return totalScore;
    }

    private static int checkFromThisTile(Shelf s,Coordinates coord, boolean[][] checked){
        Tile t=s.getTile(coord);
        int r=Shelf.getRows();
        int c=Shelf.getColumns();
        int i=coord.getX();
        int j=coord.getY();
        checked[i][j]=true;
        if(t==null) return 0;
        int res=1;
        Tile temp;

        //checking the Tile above this one
        if(i>0 && checked[i-1][j]==false){
            temp=s.getTile(new Coordinates(i-1,j));
            if(temp!=null && temp.getColor().equals(t.getColor())) res+=checkFromThisTile(s,new Coordinates(i-1,j),checked);
        }

        //checking the Tile under this one
        if(i<r-1 && checked[i+1][j]==false){
            temp=s.getTile(new Coordinates(i+1,j));
            if(temp!=null && temp.getColor().equals(t.getColor())) res+=checkFromThisTile(s,new Coordinates(i+1,j),checked);
        }

        //checking the Tile to the left of this one
        if(j>0 && checked[i][j-1]==false){
            temp=s.getTile(new Coordinates(i,j-1));
            if(temp!=null && temp.getColor().equals(t.getColor())) res+=checkFromThisTile(s,new Coordinates(i,j-1),checked);
        }

        //checking the Tile to the right of this one
        if(j<c-1 && checked[i][j+1]==false){
            temp=s.getTile(new Coordinates(i,j+1));
            if(temp!=null && temp.getColor().equals(t.getColor())) res+=checkFromThisTile(s,new Coordinates(i,j+1),checked);
        }

        return res;
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
