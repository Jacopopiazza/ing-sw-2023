package it.polimi.ingsw.Model;

import java.util.Set;

public class Game {
    private GameBoard board;
    private Player[] players;
    private PublicGoal[] goals;
    private int currentPlayer;
    private TileSack sack;

    public Game() {

    }

    public int getCurrentPlayer(){

    }

    public void setCurrentPlayer(int cp){

    }

    public Player getPlayer(int p){

    }

    public void addPlayer(Player p){

    }

    public boolean checkPublicGoals(){

    }

    public Tile popFromSack(){

    }

    public Set<Coordinates> getCoordsFromBoard(){

    }

    public Tile getTileFromBoard(Coordinates c){

    }

    public void setTileOnBoard(Coordinates c, Tile t){

    }

    public Tile pickTileFromBoard(Coordinates c){

    }

    private PublicGoal[] pickTwoPublicGoals(){

    }
}
