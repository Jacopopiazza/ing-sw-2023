package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.LobbyMessage;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.UpdateViewMessage;
import it.polimi.ingsw.Model.GlobalGoals.GlobalGoal;
import it.polimi.ingsw.Model.Utilities.Config;
import it.polimi.ingsw.ModelView.GameView;

import java.lang.String;
import java.util.*;

/**
 * The Game class represents a game session of MyShelfie.
 */
public class Game {
    private GameBoard board;
    private final int numOfPlayers;
    private Player[] players;
    private GameListener[] listeners;
    private GlobalGoal[] goals;
    private int currentPlayer;
    private TileSack sack;
    private Stack<String> cheaters;
    private boolean started;


    /**
     * Creates a new Game instance with the specified number of players.
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
     * Returns the GameView representation of the current game state.
     *
     * @return The GameView object representing the current game state.
     */
    public GameView getView() {
        return new GameView(this);
    }

    /**
     * Initializes the game by setting up private goals, creating the game board, shuffling players and listeners,
     * picking global goals, and marking the game as started.
     */
    public void init() {
        PrivateGoal[] privateGoals = PrivateGoal.getPrivateGoals(numOfPlayers);
        for( int i = 0; i < numOfPlayers; i++ )
            players[i].init(privateGoals[i]);
        board = new GameBoard(numOfPlayers);
        sack = new TileSack();
        try {
            refillGameBoard();
        } catch (EmptySackException e) {
            e.printStackTrace();
        }
        // Shuffle in the same order players and listeners
        Random rnd = new Random(System.currentTimeMillis());
        Collections.shuffle(Arrays.asList(players), rnd );
        Collections.shuffle(Arrays.asList(listeners), rnd );
        currentPlayer = 0;
        goals = this.pickTwoGlobalGoals();
        cheaters = new Stack<String>();
        started = true;
        notifyAllListeners();
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
     * Adds a player to the game with the specified username and listener.
     *
     * @param username The username of the player.
     * @param listener The listener for the player.
     */
    public void addPlayer(String username, GameListener listener) {
        int i;
        for( i=0; ( i<numOfPlayers ) && ( players[i] != null ); i++ );
        players[i] = new Player(username);
        listeners[i] = listener;
        if( currentPlayer == -1 ) currentPlayer = 0;
        notifyAllListeners();
    }

    /**
     * Reconnects a player to the game with the specified username and listener.
     *
     * @param username The username of the player.
     * @param listener The listener for the player.
     * @throws UsernameNotFoundException if the specified username is not found in the game.
     */
    public void reconnect(String username, GameListener listener) throws UsernameNotFoundException {
        int i;
        for( i=0; ( i<numOfPlayers ) && !players[i].getUsername().equals(username); i++ );
        if( i == numOfPlayers ) throw new UsernameNotFoundException();
        listeners[i] = listener;
        notifyAllListeners(new GameView(null,getNumOfActivePlayers()));
    }

    /**
     * Disconnects a player from the game with the specified username.
     *
     * @param username The username of the player to disconnect.
     * @throws UsernameNotFoundException if the username is not found in the game.
     */
    public void disconnect(String username) throws UsernameNotFoundException {
        int i;
        for( i=0; ( i<numOfPlayers ) && !players[i].getUsername().equals(username); i++ );
        if( i == numOfPlayers ) throw new UsernameNotFoundException();
        listeners[i] = null;
        notifyAllListeners(new GameView(null,getNumOfActivePlayers()));
    }

    /**
     * Kicks a player from the game with the specified username.
     *
     * @param username The username of the player to kick.
     * @throws UsernameNotFoundException if the username is not found in the game.
     */
    public void kick(String username) throws UsernameNotFoundException {
        int i;
        for( i=0; ( i<numOfPlayers ) && !players[i].getUsername().equals(username); i++ );
        if( i == numOfPlayers ) throw new UsernameNotFoundException();
        listeners[i] = null;
        players[i] = null;
        notifyAllListeners();
    }

    /**
     * Returns the number of players in the game.
     *
     * @return The number of players in the game.
     */
    public int getNumOfPlayers() {
        return numOfPlayers;
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
        int result = 0;
        for( int i=0; i<numOfPlayers; i++ ) if( listeners[i] != null ) result++;
        return result;
    }

    /**
     * Proceeds to the next player in the game.
     *
     * @return true if the game is not over, false if the game is over.
     */
    public boolean nextPlayer() {
        currentPlayer = (currentPlayer+1) % players.length;
        while( listeners[currentPlayer] == null ) currentPlayer = (currentPlayer+1) % players.length;
        if( players[currentPlayer].getShelf().isFull() )
            return false;
        notifyAllListeners(new GameView(currentPlayer,null));
        return true;
    }

    /**
     * Returns a clone of the global goals in the game.
     *
     * @return A clone of the global goals in the game.
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
     * @return The player at the specified index.
     * @throws InvalidIndexException if the index is invalid.
     */
    public Player getPlayer(int p) throws InvalidIndexException{
        if( ( p < 0 ) || ( p >= players.length ) ) {
            throw new InvalidIndexException();
        }
        return players[p];
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
     * @return The tile sack of the game.
     */
    public TileSack getTileSack() {
        return sack;
    }


    /**
     * Returns the game board of the game.
     *
     * @return The game board of the game.
     */
    public GameBoard getGameBoard() {
        return board;
    }


    /**
     * Picks tiles from the game board at the specified coordinates.
     *
     * @param coords The coordinates of the tiles to pick.
     * @return An array of picked tiles.
     * @throws InvalidCoordinatesForCurrentGameException if the coordinates are invalid for the current game.
     */
    public Tile[] pickTilesFromBoard(Coordinates[] coords) throws InvalidCoordinatesForCurrentGameException {
        Tile[] res = new Tile[coords.length];
        for( int i=0; i<coords.length; i++ ) {
            res[i] = board.getTile(coords[i]);
            board.setTile(coords[i], null);
        }
        notifyAllListeners(new GameView(board,null));
        return res;
    }

    /**
     * Inserts the given tiles into the specified column of the player's shelf.
     *
     * @param p       The player to insert the tiles.
     * @param t       The tiles to insert.
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
        int remain[] = new int[TileColor.values().length];

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
        notifyAllListeners(new GameView(board,sack));
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
        notifyAllListeners(new GameView(username));
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
        players[winner].setWinner();
    }

    /**
     * Picks two global goals randomly from the available global goals.
     *
     * @return An array of two global goals.
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
     * @param gameView The game view to notify the listeners with.
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
        if( started ){
            Message gv = new UpdateViewMessage(new GameView(this));
            for( GameListener el : listeners ){
                if( el != null ) el.update(gv);
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
