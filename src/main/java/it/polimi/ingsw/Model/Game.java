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

    public Game(int numOfPlayers) throws InvalidNumberOfPlayersException{
        if( ( numOfPlayers < 2 ) || ( numOfPlayers > Config.getInstance().getMaxNumberOfPlayers() ) ){
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

    public GameView getView(){
        return new GameView(this);
    }

    public void init(){
        PrivateGoal[] privateGoals = PrivateGoal.getPrivateGoals(numOfPlayers);
        for( int i = 0; i < numOfPlayers; i++ )
            players[i].init(privateGoals[i]);
        board = new GameBoard(numOfPlayers);
        sack = new TileSack();
        // Shuffle in the same order players and listeners
        Random rnd = new Random(System.currentTimeMillis());
        Collections.shuffle(Arrays.asList(players), rnd );
        Collections.shuffle(Arrays.asList(listeners), rnd );
        currentPlayer = 0;
        goals = this.pickTwoGlobalGoals();
        cheaters = new Stack<String>();
        notifyAllListeners();
        started = true;
    }

    public boolean isGameStarted(){
        return started;
    }

    public void addPlayer(String username, GameListener listener){
        int i;
        for( i=0; ( i<numOfPlayers ) && ( players[i] != null ); i++ );
        players[i] = new Player(username);
        listeners[i] = listener;
        if( currentPlayer == -1 ) currentPlayer = 0;
        notifyAllListeners();
    }

    public void reconnect(String username, GameListener listener) throws UsernameNotFoundException {
        int i;
        for( i=0; ( i<numOfPlayers ) && !players[i].getUsername().equals(username); i++ );
        if( i == numOfPlayers ) throw new UsernameNotFoundException();
        listeners[i] = listener;
        notifyAllListeners();
    }

    public void disconnect(String username) throws UsernameNotFoundException {
        int i;
        for( i=0; ( i<numOfPlayers ) && !players[i].getUsername().equals(username); i++ );
        if( i == numOfPlayers ) throw new UsernameNotFoundException();
        listeners[i] = null;
        notifyAllListeners();
    }

    public void kick(String username) throws UsernameNotFoundException {
        int i;
        for( i=0; ( i<numOfPlayers ) && !players[i].getUsername().equals(username); i++ );
        if( i == numOfPlayers ) throw new UsernameNotFoundException();
        listeners[i] = null;
        players[i] = null;
    }

    public int getNumOfPlayers(){
        return numOfPlayers;
    }

    public int getCurrentPlayer(){
        return currentPlayer;
    }

    public int getNumOfActivePlayers(){
        int result = 0;
        for( int i=0; i<numOfPlayers; i++ ) if( listeners[i] != null ) result++;
        return result;
    }

    // Return false if Game's over
    public boolean nextPlayer(){
        currentPlayer = (currentPlayer+1) % players.length;
        while( listeners[currentPlayer] == null ) currentPlayer = (currentPlayer+1) % players.length;
        notifyAllListeners();
        if( players[currentPlayer].getShelf().isFull() )
            return false;
        return true;
    }

    public GlobalGoal[] getGoals() throws CloneNotSupportedException {
        GlobalGoal[] temp = new GlobalGoal[this.goals.length];
        for( int i = 0; i < this.goals.length; i++ )
            temp[i] = goals[i].clone();
        return temp;
    }

    public Player getPlayer(int p) throws InvalidIndexException{
        if( ( p < 0 ) || ( p >= players.length ) ){
            throw new InvalidIndexException();
        }
        return players[p];
    }

    public void checkGlobalGoals() throws EmptyStackException, InvalidScoreException, InvalidIndexException, MissingShelfException, ColumnOutOfBoundsException {
        int token;
        int currentScore = players[currentPlayer].getScore();
        for( int i = 0; i < goals.length; i++ ){
            if( ( players[currentPlayer].getAccomplishedGlobalGoals()[i] == 0 ) && goals[i].check(players[currentPlayer].getShelf()) ){
                token = goals[i].popScore();
                players[currentPlayer].setAccomplishedGlobalGoal(i, token);
                currentScore += token;
            }
        }
        players[currentPlayer].setScore(currentScore);
        notifyAllListeners();
    }

    public TileSack getTileSack(){
        return sack;
    }

    public GameBoard getGameBoard(){
        return board;
    }

    public Tile[] pickTilesFromBoard(Coordinates[] coords) throws InvalidCoordinatesForCurrentGameException {
        Tile[] res = new Tile[coords.length];
        for( int i=0; i<coords.length; i++ ){
            res[i] = board.getTile(coords[i]);
            board.setTile(coords[i], null);
        }
        notifyAllListeners();
        return res;
    }

    public void playerInsertion(Player p, Tile[] t, int column) throws IllegalColumnInsertionException, NoTileException {
        p.insert(t, column);
    }

    public boolean refillGameBoard() throws EmptySackException {
        if( !this.board.toRefill() )
            return false;

        boolean noTileAdded = true;
        int remain[] = new int[TileColor.values().length];

        // For each Coordinate in the board
        for( Coordinates c : this.board.getCoords() ){
            // Check if the sack is empty
            if( Arrays.stream( this.sack.getRemaining()).sum() == 0 ) throw new EmptySackException();

            // Add a new Tile from the sack to the board
            try{
                if( this.board.getTile(c) == null ) {
                    this.board.setTile(c, this.sack.pop());
                    if( noTileAdded ) noTileAdded = false;
                }
            } catch( InvalidCoordinatesForCurrentGameException e ){
                e.printStackTrace();
            }
        }
        notifyAllListeners();
        // at least one Tile was added
        return true;
    }

    public void addCheater(String username){
        cheaters.add(username);
        notifyAllListeners();
    }

    public Stack<String> getCheaters(){
        return cheaters;
    }

    // Used by the controller only in case of TimeOut, otherwise the winner is set by endGame()
    public void setWinner(int p){
        players[p].setWinner();
        notifyAllListeners();
    }

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

    private GlobalGoal[] pickTwoGlobalGoals() throws InvalidNumberOfPlayersException {
        List<GlobalGoal> goals = GlobalGoal.getInstances(players.length);

        Collections.shuffle(goals);
        GlobalGoal[] returned = new GlobalGoal[Config.getInstance().getNumOfGlobalGoals()];
        for( int i = 0; i < returned.length; i++ )
            returned[i] = goals.get(i);

        return returned;
    }

    private void notifyAllListeners(){
        if( started ){
            Message gv = new UpdateViewMessage(new GameView(this));
            for( GameListener el : listeners ){
                if( el != null ) el.update(gv);
            }
        }
        else{
            List<String> players = new ArrayList<>();
            for( int i=0; i<numOfPlayers; i++ ){
                if( this.players[i] != null )
                    players.add(this.players[i].getUsername());
            }
            Message lobby = new LobbyMessage(players);
        }
    }

}
