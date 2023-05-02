package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.network.Server;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.EventListener;
import java.util.stream.Collectors;

public class Controller {

    private Server gameServer;
    private boolean gameStarted;
    private Game model;

    public Controller (int numOfPlayers, Server server){
        gameServer = server;
        gameStarted = false;
        model = new Game(numOfPlayers);
    }

    public boolean isGameStarted(){
        return gameStarted;
    }

    //returns true if the lobby is full
    public boolean addPlayer(String nick, EventListener listener){
        int playersInLobby = model.addPlayer(nick,listener);
        int maxPlayersInLobby = model.getNumOfPlayers();
        if(playersInLobby == maxPlayersInLobby){
            model.init();
            gameStarted = true;
            return true;
        }
        return false;
    }

    public void kick(String nick){
        if(!gameStarted) model.kick(nick);
    }

    public void disconnect(String nick){
        if(gameStarted) model.disconnect(nick);
    }

    public void doTurn(String nick, Coordinates[] chosenTiles, int col) throws NotYourTurnException, NotValidChosenTiles, IllegalColumnInsertionException{
        Player currPlayer = null;
        try {
            currPlayer = model.getPlayer(model.getCurrentPlayer());
        } catch (InvalidIndexException e) {
            e.printStackTrace();
        }
        //checking that the given player is actually the one who has to play
        if( !(currPlayer.getNickname().equals(nick)) ){
            throw new NotYourTurnException();
        }

        GameBoard board = model.getGameBoard();

        //checking that the length of the array is at most 3
        if(chosenTiles.length > 3) throw new NotValidChosenTiles();

        //checking there are no duplicates and that they are all pickable
        for (Coordinates c : chosenTiles){
            try {
                if(Arrays.stream(chosenTiles).filter(x -> x.equals(c)).collect(Collectors.toList()).size() > 1
                    || !(board.isPickable(c)) ) {
                    throw new NotValidChosenTiles();
                }
            } catch (InvalidCoordinatesForCurrentGameException e) {
                throw new NotValidChosenTiles();
            }
        }

        //checking that the chosen tiles are on the same column or row on the board,
        //that they are one next ot the other
        boolean row = true;
        boolean column = true;
        for(int i=0;i<chosenTiles.length-1 && (row || column);i++) {
            if(chosenTiles[i].getROW() != chosenTiles[i+1].getROW()) row = false;
            if(chosenTiles[i].getCOL() != chosenTiles[i+1].getCOL()) column = false;

            if( !(row || column) ) throw new NotValidChosenTiles();

            Coordinates c = chosenTiles[i];
            if(row && Arrays.stream(chosenTiles).filter(x -> c.getROW()-1 == x.getROW() ||
                    c.getROW()+1 == x.getROW()).collect(Collectors.toList()).size() == 0) throw new NotValidChosenTiles();

            if(column && Arrays.stream(chosenTiles).filter(x -> c.getCOL()-1 == x.getCOL() ||
                    c.getCOL()+1 == x.getCOL()).collect(Collectors.toList()).size() == 0) throw new NotValidChosenTiles();
        }

        //now I know the chosen tiles are valid
        //checking the selected column
        if(col<0 || col>Shelf.getColumns()) throw new IllegalColumnInsertionException();

        //taking the tiles from the board
        Tile[] effectiveTiles = new Tile[chosenTiles.length];
        for(int i=0; i<chosenTiles.length; i++) {
            try {
                effectiveTiles[i]=board.getTile(chosenTiles[i]);
                board.setTile(chosenTiles[i],null);
            } catch (InvalidCoordinatesForCurrentGameException e) {
                e.printStackTrace();
            }
        }

        //putting the tiles in the shelf;
        try {
            currPlayer.insert(effectiveTiles,col);
        } catch (NoTileException e) {
            throw new NotValidChosenTiles();
        }

        //checking the global goals
        try {
            model.checkGlobalGoals();
        }
        catch (InvalidIndexException e) {
            e.printStackTrace();
        }
        catch (InvalidScoreException e1) {
            e1.printStackTrace();
        }
        catch (EmptyStackException e2) {
            e2.printStackTrace();
        }
        catch (MissingShelfException e3) {
            e3.printStackTrace();
        }

        //refilling the board
        model.refillGameBoard();

        //setting the next player
        if(model.getNumOfActivePlayers() ==0);//need to end the game
        if(model.getNumOfActivePlayers() == 1);//need to implement the timer
        if(model.getNumOfActivePlayers() > 1) model.nextPlayer();
    }
}