package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Network.GameServer;

import java.util.*;

/**
 * The Controller class handles the game logic and manages player actions.
 */
public class Controller  {
    private final GameServer gameServer;
    private final Game model;
    private final int timerLength = 30; // in seconds
    private boolean onlyLastPlayerIsDone;
    private final Timer timer = new Timer();

    /**
     * A {@code TimerTask} that runs when the timer expires. It cancels the timer, sets the current player as the winner,
     * retrieves the list of player usernames, and notifies the server to delete the game.
     */
    private final TimerTask task = new TimerTask(){
        public void run(){
            timer.cancel();
            model.setWinner(model.getCurrentPlayer());
            endGame();
        }
    };

    /**
     * Constructs a new {@code Controller} object with the specified Game model and GameServer.
     *
     * @param model  the Game model to associate with the controller
     * @param server the GameServer to communicate with
     */
    public Controller(Game model, GameServer server){
        gameServer = server;
        this.model = model;
        onlyLastPlayerIsDone = false;
    }

    /**
     * Checks if the game has started.
     *
     * @return true if the game has started, false otherwise
     */
    public boolean isGameStarted(){
        return model.isGameStarted();
    }

    /**
     * Adds a player to the game lobby.
     *
     * @param username The username of the player.
     * @param listener The {@link GameListener} for the player.
     * @return true if the lobby is full and the game is ready to start, false otherwise.
     */
    public boolean addPlayer(String username, GameListener listener){
        model.addPlayer(username,listener);
        if( model.getNumOfActivePlayers() == model.getNumOfPlayers() ){
            model.init();
            return true;
        }
        return false;
    }

    /**
     * Retrieves the {@code GameListener} associated with the specified username.
     *
     * @param username the username of the player whose GameListener is to be retrieved
     * @return the {@link GameListener} associated with the specified username
     * @throws UsernameNotFoundException if the specified username is not found
     */
    public GameListener getListener(String username) throws UsernameNotFoundException{
        return model.getListener(username);
    }

    /**
     * Returns the number of active players in the game.
     *
     * @return The number of active players.
     */
    public int getNumOfActivePlayers(){
        return model.getNumOfActivePlayers();
    }

    /**
     * Kicks a player from the game.
     *
     * @param username The username of the player to kick.
     * @return The {@link GameListener} object of the kicked player, null if the game is not started yet.
     */
    public GameListener kick(String username){
        if( !model.isGameStarted() ){
            try {
               return model.kick(username);
            } catch (UsernameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Reconnects a player to the game.
     *
     * @param username The username of the player.
     * @param listener The {@link GameListener} object for the player.
     */
    public void reconnect(String username, GameListener listener){
        try {
            model.reconnect(username,listener);
        } catch (UsernameNotFoundException e) {
            e.printStackTrace();
            return;
        }
        if( model.getNumOfActivePlayers() == 2 ){
            timer.cancel();
            // If a player reconnects after the last remaining player has performed their turn,
            // the currentPlayer must be updated because it was not changed at the end of that turn
            if( onlyLastPlayerIsDone ){
                // This flag must be set back to false
                onlyLastPlayerIsDone = false;
                // Therefore the nextPlayer is set
                if( !model.nextPlayer() )
                    endGame();
            }
        }
    }

    /**
     * Disconnects a player from the game.
     *
     * @param username The username of the player to disconnect.
     */
    public void disconnect(String username){
        if( model.isGameStarted() ) {
            try {
                model.disconnect(username);
            } catch (UsernameNotFoundException e) {
                e.printStackTrace();
                return;
            }
            if( model.getNumOfActivePlayers() == 1 ){
                if( model.getPlayer(model.getCurrentPlayer()).getUsername().equals(username) ){
                    if (!model.nextPlayer()){
                        endGame();
                        return;
                    }
                }
                timer.schedule(task,timerLength*1000);
                return;
            }
            if( model.getNumOfActivePlayers() == 0 )
                endGame();
            if( model.getPlayer(model.getCurrentPlayer()).getUsername().equals(username) ) {
                if( !model.nextPlayer() )
                    endGame();
            }
        }
    }

    /**
     * Executes a player's turn.
     *
     * @param username     The username of the player.
     * @param chosenTiles  The {@link Coordinates} of the tiles the player wants to choose.
     * @param col          The column index where the player wants to insert the chosen tiles.
     */
    public void doTurn(String username, Coordinates[] chosenTiles, int col){
        // If timer is running, the player should just wait for THEM to win
        if( ( model.getNumOfPlayers() == 1 )){
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
        if( !( currPlayer.getUsername().equals(username)) ){
            model.addCheater(username);
            return;
        }

        // Check if the chosen tiles are valid
        GameBoard board = model.getGameBoard();
        if( !(board.checkChosenTiles(chosenTiles)) ){
            model.addCheater(username);
            return;
        }

        // Check if the index for the column is valid...
        if( ( col<0 ) || ( col>=Shelf.getColumns() ) ){
            model.addCheater(username);
            return;
        }
        // ...and check if there is enough space in such column
        try{
            if( chosenTiles.length > currPlayer.getShelf().remainingSpaceInColumn(col) ){
                model.addCheater(username);
                return;
            }
        } catch (ColumnOutOfBoundsException e){
            e.printStackTrace();
        }

        // Pick the given Tiles
        Tile[] effectiveTiles = null;
            try{
                effectiveTiles = model.pickTilesFromBoard(chosenTiles);
            } catch (InvalidCoordinatesForCurrentGameException e) {
                e.printStackTrace();
            }

        // Insert the tiles in the shelf
        try {
            model.playerInsertion(currPlayer, effectiveTiles, col);
        } catch (NoTileException | ColumnOutOfBoundsException | IllegalColumnInsertionException e) {
            e.printStackTrace();
        }
        // Compute the global goals' points
        try {
            model.checkGlobalGoals();
        } catch (InvalidIndexException | InvalidScoreException | EmptyStackException | MissingShelfException e) {
            e.printStackTrace();
        }

        // Eventually refill the board
        try {
            model.refillGameBoard();
        } catch (EmptySackException e) {
            e.printStackTrace();
        }

        // Set the next player and if the game is over the game ends
        if( ( model.getNumOfActivePlayers() > 1 ) && !model.nextPlayer() )
            endGame();

        // In case it's the turn of the remaining player, he was free to do it
        if( model.getNumOfActivePlayers() == 1 )
            onlyLastPlayerIsDone = true;
    }

    /**
     * Ends the game, calculates final scores, and performs necessary cleanup.
     * Notifies the server to delete the game.
     */
    private void endGame(){
        model.endGame();
        // Get list of usernames for server's method
        List<String> players = new ArrayList<>();
        List<GameListener> listeners = new ArrayList<>();
        for( int i=0; i<model.getNumOfPlayers(); i++ ){
            players.add(model.getPlayer(i).getUsername());
            if(model.getListener(i) != null)
                listeners.add(model.getListener(i));
        }
        // Delete game from server
        gameServer.deleteGame(players,listeners);
    }

}