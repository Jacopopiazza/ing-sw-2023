package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.LobbyMessage;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.UpdateViewMessage;
import it.polimi.ingsw.Model.GlobalGoals.GlobalGoal;
import it.polimi.ingsw.Utilities.Config;
import it.polimi.ingsw.ModelView.GameView;

import java.lang.String;
import java.util.*;

/**
 * The Game class represents a game session of MyShelfie.
 */
@SuppressWarnings("ALL")
public class Game {
    private GameBoard board;
    private final int numOfPlayers;
    private final Player[] players;
    private final GameListener[] listeners;
    private GlobalGoal[] goals;
    private int currentPlayer;
    private TileSack sack;
    private Stack<String> cheaters;
    private boolean started;


    /**
     * Creates a new {@code Game} object for the specified number of players.
     *
     * @param numOfPlayers The number of players in the game.
     * @throws InvalidNumberOfPlayersException if the number of players is invalid.
     */
    public Game(int numOfPlayers) throws InvalidNumberOfPlayersException{
        if( ( numOfPlayers < 2 ) || ( numOfPlayers > Config.getInstance().getMaxNumberOfPlayers() ) ) {
            throw new InvalidNumberOfPlayersException();
        }
        board = null;
        this.numOfPlayers = numOfPlayers;
        listeners = new GameListener[numOfPlayers];
        players = new Player[numOfPlayers];
        goals = null;
        currentPlayer = -1;
        sack = null;
        cheaters = null;
        started = false;
    }

    /**
     * Returns the ModelView representation of the current game state.
     *
     * @return The {@link GameView} object representing the current game state.
     */
    public GameView getView() {
        return new GameView(this);
    }

    /**
     * Initializes the game by setting up private goals, creating the game board, randomly choosing the first player,
     * picking global goals, and marking the game as started.
     *
     * @throws InvalidNumberOfPlayersException if a players is null.
     */
    public void init() throws NullPlayersException{

        synchronized (players) {
            PrivateGoal[] privateGoals = PrivateGoal.getPrivateGoals(numOfPlayers);
            for (int i = 0; i < numOfPlayers; i++){
                if(players[i] == null) throw new NullPlayersException();
                players[i].init(privateGoals[i]);
            }

            board = new GameBoard(numOfPlayers);
            sack = new TileSack();
            try {
                refillGameBoard();
            } catch (EmptySackException e) {
                e.printStackTrace();
            }

            //Randomly select first player
            currentPlayer = new Random().nextInt(numOfPlayers);
            goals = this.pickTwoGlobalGoals();
            cheaters = new Stack<String>();
            started = true;
            notifyAllListeners();
        }
    }

    /**
     * Checks if the game has started.
     *
     * @return true if the game has started, false otherwise.
     */
    public boolean isGameStarted() {
        return started;
    }

    /**
     * Adds a player to the game with the specified username, set his listener and notifies
     * all the other listeners.
     *
     * @param username The username of the player.
     * @param listener The {@link GameListener} for the player.
     */
    public void addPlayer(String username, GameListener listener) {
        synchronized (players){
            int i;
            for( i=0; ( i<numOfPlayers ) && ( players[i] != null ); i++ );
            players[i] = new Player(username);
            listeners[i] = listener;
            if( currentPlayer == -1 ) currentPlayer = 0;
            notifyAllListeners();
        }
    }

    /**
     * Reconnects the player with the specified username, sets his listener,
     * and notifies all the other listeners of the reconnection.
     *
     * @param username The username of the player.
     * @param listener The {@link GameListener} for the player.
     * @throws UsernameNotFoundException if the specified username is not found in the game.
     */
    public void reconnect(String username, GameListener listener) throws UsernameNotFoundException {
        synchronized (players){
            int i;
            for( i=0; ( i<numOfPlayers ) && !players[i].getUsername().equals(username); i++ );
            if( i == numOfPlayers )
                throw new UsernameNotFoundException();

            notifyAllListeners(new GameView((Integer)(null),getNumOfActivePlayers()+1));
            listeners[i] = listener;
            listener.update(new UpdateViewMessage(new GameView(this)));
        }

    }

    /**
     * Disconnects the player with the specified username from the game and notifies
     * all the other listeners of the game.
     *
     * @param username The username of the player to disconnect.
     * @throws UsernameNotFoundException if the username is not found in the game.
     */
    public void disconnect(String username) throws UsernameNotFoundException {
        synchronized (players){
            int i;
            for( i=0; ( i<numOfPlayers ) && !players[i].getUsername().equals(username); i++ );
            if( i == numOfPlayers ) throw new UsernameNotFoundException();
            listeners[i] = null;
            notifyAllListeners(new GameView((Integer)(null),getNumOfActivePlayers()));
        }

    }

    /**
     * Kicks the player the specified username from the lobby.
     *
     * @param username The username of the player to kick.
     * @throws UsernameNotFoundException if the username is not found in the game.
     * @return the {@link GameListener} of the kicked player
     */
    public GameListener kick(String username) throws UsernameNotFoundException {
        synchronized (players){
            GameListener listener;
            int i;
            for( i=0; ( i<numOfPlayers ) && !players[i].getUsername().equals(username); i++ );
            if( i == numOfPlayers ) throw new UsernameNotFoundException();
            listener = listeners[i];
            listeners[i] = null;
            players[i] = null;
            notifyAllListeners();
            return listener;
        }


    }

    /**
     * Returns the number of players in the game.
     *
     * @return The number of players in the game.
     */
    public int getNumOfPlayers() {
        synchronized (players){
            return numOfPlayers;
        }
    }

    /**
     * Returns the index of the current player.
     *
     * @return The index of the current player.
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Returns the number of active players in the game.
     *
     * @return The number of active players in the game.
     */
    public int getNumOfActivePlayers() {
        synchronized (players){
            int result = 0;
            for( int i=0; i<numOfPlayers; i++ ) if( listeners[i] != null ) result++;
            return result;
        }

    }

    /**
     * Proceeds to the next player in the game.
     *
     * @return true if the game is not over, false if the game is over.
     */
    public boolean nextPlayer() {
        synchronized (players){
            currentPlayer = (currentPlayer+1) % players.length;
            while( listeners[currentPlayer] == null ) currentPlayer = (currentPlayer+1) % players.length;
            if( players[currentPlayer].getShelf().isFull() )
                return false;
            notifyAllListeners(new GameView(currentPlayer,null));
        }
        return true;
    }

    /**
     * Returns a clone of the global goals in the game.
     *
     * @return A clone of the {@link GlobalGoal} of the game.
     * @throws CloneNotSupportedException if cloning fails.
     */
    public GlobalGoal[] getGoals() throws CloneNotSupportedException {
        GlobalGoal[] temp = new GlobalGoal[this.goals.length];
        for( int i = 0; i < this.goals.length; i++ )
            temp[i] = goals[i].clone();
        return temp;
    }

    /**
     * Returns the player at the specified index.
     *
     * @param p The index of the player to retrieve.
     * @return The {@link Player} at the specified index.
     * @throws InvalidIndexException if the index is invalid.
     */
    public Player getPlayer(int p) throws InvalidIndexException{
        synchronized (players){
            if( ( p < 0 ) || ( p >= players.length ) ) {
                throw new InvalidIndexException();
            }
            return players[p];
        }

    }

    /**
     * Returns the listener at the specified index.
     *
     * @param p The index of the listener to retrieve.
     * @return The {@link GameListener} at the specified index.
     * @throws InvalidIndexException if the index is invalid.
     */
    public GameListener getListener(int p) throws InvalidIndexException{
        synchronized (players){
            if( ( p < 0 ) || ( p >= players.length ) ) {
                throw new InvalidIndexException();
            }
            return listeners[p];
        }

    }

    /**
     * Returns the listener of the player with the specified username.
     *
     * @param username The username of the player to retrieve.
     * @return The {@link GameListener} of the player with the specified username.
     * @throws UsernameNotFoundException if the username is not found in the game.
     */
    public GameListener getListener(String username) throws UsernameNotFoundException{
        synchronized (players){
            int i;
            for(i = 0;i<players.length;i++){
                if(players[i] != null && players[i].getUsername().equals(username)) break;
            }

            if(i >= players.length){
                throw new UsernameNotFoundException();
            }

            return listeners[i];
        }

    }

    /**
     * Checks the global goals for the current player and updates their score accordingly.
     *
     * @throws EmptyStackException              if the global goal stack is empty.
     * @throws InvalidScoreException            if the score of a global goal is invalid.
     * @throws InvalidIndexException            if the index is invalid.
     * @throws MissingShelfException            if the player's shelf is missing.
     * @throws ColumnOutOfBoundsException      if the column index is out of bounds.
     */
    public void checkGlobalGoals() throws EmptyStackException, InvalidScoreException, InvalidIndexException, MissingShelfException, ColumnOutOfBoundsException {
        int token;
        int currentScore = players[currentPlayer].getScore();
        for( int i = 0; i < goals.length; i++ ) {
            if( ( players[currentPlayer].getAccomplishedGlobalGoals()[i] == 0 ) && goals[i].check(players[currentPlayer].getShelf()) ) {
                token = goals[i].popScore();
                players[currentPlayer].setAccomplishedGlobalGoal(i, token);
                currentScore += token;
            }
        }
        players[currentPlayer].setScore(currentScore);
        notifyAllListeners(new GameView(players[currentPlayer],currentPlayer,goals));
    }

    /**
     * Returns the tile sack of the game.
     *
     * @return The {@link TileSack} of the game.
     */
    public TileSack getTileSack() {
        return sack;
    }

    /**
     * Returns the game board of the game.
     *
     * @return The {@link GameBoard} of the game.
     */
    public GameBoard getGameBoard() {
        return board;
    }

    /**
     * Picks tiles from the game board at the specified coordinates.
     *
     * @param coords The coordinates of the tiles to pick.
     * @return An array of picked {@link Tile}.
     * @throws InvalidCoordinatesForCurrentGameException if the coordinates are invalid for the current game.
     */
    public Tile[] pickTilesFromBoard(Coordinates[] coords) throws InvalidCoordinatesForCurrentGameException {
        Tile[] res = new Tile[coords.length];
        for( int i=0; i<coords.length; i++ ) {
            res[i] = board.getTile(coords[i]);
            board.setTile(coords[i], null);
        }
        notifyAllListeners(new GameView(board));
        return res;
    }

    /**
     * Inserts the given tiles into the specified column of the player's shelf.
     *
     * @param p       The {@link Player} to insert the tiles.
     * @param t       The array of {@link Tile} to insert.
     * @param column  The column index to insert the tiles.
     * @throws IllegalColumnInsertionException if the column insertion is illegal.
     * @throws NoTileException                 if no tiles are provided to insert.
     */
    public void playerInsertion(Player p, Tile[] t, int column) throws IllegalColumnInsertionException, NoTileException {
        p.insert(t, column);
    }

    /**
     * Refills the game board with tiles from the tile sack.
     *
     * @return true if tiles were added to the game board, false otherwise.
     * @throws EmptySackException if the tile sack is empty.
     */
    public boolean refillGameBoard() throws EmptySackException {
        if( !this.board.toRefill() )
            return false;

        boolean noTileAdded = true;

        // For each Coordinate in the board
        for( Coordinates c : this.board.getCoords() ) {
            // Check if the sack is empty
            if( Arrays.stream( this.sack.getRemaining()).sum() == 0 ) throw new EmptySackException();

            // Add a new Tile from the sack to the board
            try{
                if( this.board.getTile(c) == null ) {
                    this.board.setTile(c, this.sack.pop());
                    if( noTileAdded ) noTileAdded = false;
                }
            } catch (InvalidCoordinatesForCurrentGameException e) {
                e.printStackTrace();
            }
        }
        notifyAllListeners(new GameView(board));
        // at least one Tile was added
        return true;
    }

    /**
     * Adds a cheater to the game.
     *
     * @param username The username of the cheater.
     */
    public void addCheater(String username){
        cheaters.add(username);
        notifyAllListeners(new GameView(username,currentPlayer));
    }

    /**
     * Sets the winner of the game at the specified index.
     *
     * @param p The index of the player who won the game.
     */
    public void setWinner(int p){
        players[p].setWinner();
        notifyAllListeners(new GameView(players));
    }

    /**
     * Ends the game and calculates the final scores and the winner.
     */
    public void endGame(){
        // Add point for being the first to finish
        players[currentPlayer].first();

        // Add points for PrivateGoals and for the goals on the GameBoard
        for( int i=0; i<numOfPlayers; i++ ){
            players[i].checkPrivateGoal();
            players[i].setScore( players[i].getScore() + GameBoard.checkBoardGoal(players[i].getShelf()) );
        }

        // "In case of a tie, the tied player sitting closer (clockwise)
        // from the first player wins the game."
        // Remember: the player who started has index 0
        int winner = 0;
        for( int i=0; i<numOfPlayers; i++ ){
            if( players[i].getScore() > players[winner].getScore() )
                winner = i;
        }

        // Set the game winner
        this.setWinner(winner);
    }

    /**
     * Picks two global goals randomly from the available global goals.
     *
     * @return An array of two {@link GlobalGoal}.
     */
    private GlobalGoal[] pickTwoGlobalGoals() throws InvalidNumberOfPlayersException {
        List<GlobalGoal> goals = GlobalGoal.getInstances(players.length);

        Collections.shuffle(goals);
        GlobalGoal[] returned = new GlobalGoal[Config.getInstance().getNumOfGlobalGoals()];
        for( int i = 0; i < returned.length; i++ )
            returned[i] = goals.get(i);

        return returned;
    }

    /**
     * Notifies all the game listeners with the provided game view.
     *
     * @param gameView The {@link GameView} to notify the listeners with.
     */
    private void notifyAllListeners(GameView gameView){
        if(started){
            Message gv = new UpdateViewMessage(gameView);
            for( GameListener el : listeners ){
                if( el != null ) el.update(gv);
            }
        }
    }

    /**
     * Notifies all the game listeners.
     * If the game has started, it sends an update message with the current game view.
     * If the game has not started, it sends a lobby message with the player usernames.
     */
    private void notifyAllListeners(){
        synchronized (players){
            if( started ){
                Message gv = new UpdateViewMessage(new GameView(this));
                for(int i = 0;i < listeners.length;i++){
                    if( listeners[i] != null ) listeners[i].update(gv);
                }
            }
            else{
                List<String> players = Arrays.stream(this.players).filter(x -> x!=null).map(x -> x.getUsername()).toList();
                Message lobby = new LobbyMessage(players);
                for( GameListener el : listeners ){
                    if( el != null ) el.update(lobby);
                }
            }
        }
    }

}
