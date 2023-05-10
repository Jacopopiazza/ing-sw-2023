package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Network.ServerImplementation;

import java.util.*;

public class Controller {
    private ServerImplementation gameServer;
    private Game model;
    private final int timerLength = 30; // in seconds
    private boolean onlyLastPlayerHasDoneTheTurnAlready;
    private final Timer timer = new Timer();

    private final TimerTask task = new TimerTask(){
        public void run(){
            timer.cancel();
            model.setWinner(model.getCurrentPlayer());
            List<String> players = new ArrayList<String>();
            for( int i=0; i<model.getNumOfPlayers(); i++ )
                players.add( model.getPlayer(i).getUsername() );
            gameServer.deleteGame(players);
        }
    };

    public Controller (int numOfPlayers, ServerImplementation server){
        gameServer = server;
        model = new Game(numOfPlayers);
        onlyLastPlayerHasDoneTheTurnAlready = false;
    }

    public boolean isGameStarted(){
        return model.isGameStarted();
    }

    //returns true if the lobby is full
    public boolean addPlayer(String username, GameListener listener){
        model.addPlayer(username,listener);
        if( model.getNumOfActivePlayers() == model.getNumOfPlayers() ){
            model.init();
            return true;
        }
        return false;
    }

    public void kick(String username){
        if( !model.isGameStarted() ){
            try {
                model.kick(username);
            } catch (UsernameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    public void reconnect(String username, GameListener listener){
        try {
            model.reconnect(username,listener);
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if( model.getNumOfActivePlayers() == 2 ){
            timer.cancel();
            // If a player reconnects after the last remaining player performed their turn,
            // the currentPlayer won't change at the end of it
            if( onlyLastPlayerHasDoneTheTurnAlready ) {
                // Therefore the nextPlayer is set
                model.nextPlayer();
                // And this flag must be set back to false
                onlyLastPlayerHasDoneTheTurnAlready = false;
            }
        }
    }

    public void disconnect(String username){
        if( model.isGameStarted() ) {
            try {
                model.disconnect(username);
            }
            catch (UsernameNotFoundException e) {
                e.printStackTrace();
                return;
            }
            if( model.getNumOfActivePlayers() == 1 ) {
                if( model.getPlayer(model.getCurrentPlayer()).getUsername().equals(username) )
                    model.nextPlayer();
                timer.schedule(task,0,timerLength*1000);
                return;
            }
            if( model.getNumOfActivePlayers() == 0 ){
                List<String> players = new ArrayList<String>();
                for( int i=0; i<model.getNumOfPlayers(); i++ )
                    players.add(model.getPlayer(i).getUsername());
                gameServer.deleteGame(players);
                return;
            }
            if( model.getPlayer(model.getCurrentPlayer()).getUsername().equals(username) )
                model.nextPlayer();
        }
    }

    public int getNumOfActivePlayers(){
        return model.getNumOfActivePlayers();
    }

    public void doTurn(String username, Coordinates[] chosenTiles, int col){
        // If timer is running, the player should just wait for THEM to win
        // It's not possible that onlyLastPlayerHasDoneTheTurnAlready == True while the first condition is false
        if ( ( model.getNumOfPlayers() == 1 ) && onlyLastPlayerHasDoneTheTurnAlready ){
            model.addCheater(username);
            return;
        }

        // Get the current player...
        Player currPlayer = null;
        try {
            currPlayer = model.getPlayer(model.getCurrentPlayer());
        } catch (InvalidIndexException e) {
            e.printStackTrace();
        }
        // ...and check if such player is the one who sent the request
        if( !( currPlayer.getUsername().equals(username) || ( model.getNumOfActivePlayers() == 1 ) ) ){
            model.addCheater(username);
            return;
        }

        // Check if the chosen tiles are valid
        GameBoard board = model.getGameBoard();
        if( !(board.checkChosenTiles(chosenTiles)) ){
            model.addCheater(username);
            return;
        }

        // Check is the index for the column is valid...
        if( ( col<0 ) || ( col>Shelf.getColumns() ) ){
            model.addCheater(username);
            return;
        }
        // ...and check if there is enough space in such column
        try{
            if( chosenTiles.length > currPlayer.getShelf().remainingSpaceInColumn(col) ){
                model.addCheater(username);
                return;
            }
        } catch(ColumnOutOfBoundsException e){
            e.printStackTrace();
        }

        // Pick the given Tiles
        Tile[] effectiveTiles = null;
            try{
                effectiveTiles = model.pickTilesFromBoard(chosenTiles);
            } catch(InvalidCoordinatesForCurrentGameException e) {
                e.printStackTrace();
            }

        // Insert the tiles in the shelf
        try {
            model.playerInsertion(currPlayer, effectiveTiles, col);
        } catch(NoTileException e) {
            e.printStackTrace();
        } catch(ColumnOutOfBoundsException e1) {
            e1.printStackTrace();
        } catch(IllegalColumnInsertionException e2) {
            e2.printStackTrace();
        }

        // Compute the global goals' points
        try {
            model.checkGlobalGoals();
        } catch (InvalidIndexException e) {
            e.printStackTrace();
        } catch (InvalidScoreException e1 ){
            e1.printStackTrace();
        } catch (EmptyStackException e2) {
            e2.printStackTrace();
        } catch (MissingShelfException e3) {
            e3.printStackTrace();
        } catch (ColumnOutOfBoundsException e4) {
            e4.printStackTrace();
        }

        // Eventually refill the board
        try {
            model.refillGameBoard();
        } catch( EmptySackException e ) {
            e.printStackTrace();
        }

        // Set the next player and if the game is over the game ends
        if( ( model.getNumOfActivePlayers() > 1 ) && !model.nextPlayer() ){
            model.endGame();
            // Get list of usernames for server's method
            List<String> players = new ArrayList<String>();
            for( int i=0; i<model.getNumOfPlayers(); i++ )
                players.add(model.getPlayer(i).getUsername());
            // Delete game from server
            gameServer.deleteGame(players);
        }

        // In case it's the turn of the remaining player, he was free to do it
        if( model.getNumOfActivePlayers() == 1 )
            onlyLastPlayerHasDoneTheTurnAlready = true;
    }
}