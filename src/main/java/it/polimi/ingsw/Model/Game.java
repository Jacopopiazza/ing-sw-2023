package it.polimi.ingsw.Model;

import java.util.Optional;
import java.util.Set;

public class Game {
    private GameBoard board;
    private Player[] players; // to be filled through addPlayer()
    private PublicGoal[] goals;
    private int currentPlayer;
    private TilesSack sack;

    public Game() {

    }

    public int getCurrentPlayer(){

    }

    public void setCurrentPlayer(){

    }

    public Player getPlayer(int p){

    }

    public void addPlayer(Player p){

    }

    public void checkPublicGoals(){

    }

    public Tile popFromSack(){

    }

    public Set<Coordinates> getNumberOfTilesFromBoard(){

    }

    public Optional<Tile> getTileFromBoard(Coordinates c){

    }

    public void setTileOnBoard(Coordinates c, Tile t){

    }

    public Optional<Tile> pickTileFromBoard(Coordinates c){

    }

    private void pickTwoPublicGoals(){

    }



}
