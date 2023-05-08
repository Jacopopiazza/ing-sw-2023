package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Network.Server;

import java.util.EmptyStackException;

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
    public boolean addPlayer(String username, GameListener listener){
        model.addPlayer(username,listener);
        if(model.getNumOfActivePlayers() == model.getNumOfPlayers()){
            model.init();
            gameStarted = true;
            return true;
        }
        return false;
    }

    public void kick(String username){
        if(!gameStarted){
            try {
                model.kick(username);
            }
            catch (UsernameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void reconnect(String username, GameListener listener){
        try {
            model.reconnect(username,listener);
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void disconnect(String username){
        if(gameStarted) {
            try {
                model.disconnect(username);
            }
            catch (UsernameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public int getNumOfActivePlayers(){
        return model.getNumOfActivePlayers();
    }

    public void doTurn(String username, Coordinates[] chosenTiles, int col){
        Player currPlayer = null;
        try {
            currPlayer = model.getPlayer(model.getCurrentPlayer());
        } catch (InvalidIndexException e) {
            e.printStackTrace();
        }
        //checking that the given player is actually the one who has to play
        if( !( currPlayer.getUsername().equals(username) || ( model.getNumOfActivePlayers() == 1 ) ) ){
            model.addCheater(username);
            return;
        }

        GameBoard board = model.getGameBoard();

        if( !(board.checkChosenTiles(chosenTiles)) ){
            model.addCheater(username);
            return;
        }

        //now I know the chosen tiles are valid
        //checking the selected column
        if(col<0 || col>Shelf.getColumns()){
            model.addCheater(username);
            return;
        }

        //checking whether there is enough space in the selected column or not
        try {
            if(chosenTiles.length>currPlayer.getShelf().remainingSpaceInColumn(col)){
                model.addCheater(username);
                return;
            }
        } catch (ColumnOutOfBoundsException e) {
            e.printStackTrace();
        }

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
            e.printStackTrace();
        }
        catch(ColumnOutOfBoundsException e1){
            e1.printStackTrace();
        }
        catch(IllegalColumnInsertionException e2){
            e2.printStackTrace();
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
        catch (ColumnOutOfBoundsException e4) {
            e4.printStackTrace();
        }

        //refilling the board
        try{
            model.refillGameBoard();
        }
        catch( EmptySackException e ){
            e.printStackTrace();
        }

        //setting the next player
        if( model.getNumOfActivePlayers() > 1 ) model.nextPlayer();
    }
}