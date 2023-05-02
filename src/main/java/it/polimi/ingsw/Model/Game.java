package it.polimi.ingsw.Model;

import it.polimi.ingsw.Exceptions.*;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.UpdateViewMessage;
import it.polimi.ingsw.Model.Utilities.Config;
import it.polimi.ingsw.ModelView.GameView;

import java.lang.String;
import java.util.*;

public class Game {
    private GameBoard board;
    private final int numOfPlayers;
    private GameListener[] listeners;
    private Player[] players;
    private GlobalGoal[] goals;
    private int currentPlayer;
    private TileSack sack;

    private Stack<String> cheaters;

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
        sack= null;
        cheaters = null;
    }

    public void init(){
        PrivateGoal[] privateGoals = PrivateGoal.getPrivateGoals(numOfPlayers);
        for( int i = 0; i < numOfPlayers; i++ )
            players[i].init(privateGoals[i]);

        board = new GameBoard(numOfPlayers);
        sack = new TileSack();
        currentPlayer = new Random().nextInt(numOfPlayers);
        goals = this.pickTwoGlobalGoals();
        cheaters = new Stack<String>();
    }

    public int addPlayer(String nick, GameListener listener){
        int i;
        for(i=0; i<numOfPlayers && players[i]!=null; i++);
        players[i] = new Player(nick);
        listeners[i] = listener;
        int result = 0;
        for(i=0; i<numOfPlayers; i++){
            if(players[i] != null) result++;
        }
        return result;
    }

    public void disconnect(String nick){
        for(int i=0; i<numOfPlayers;i++){
            if(nick.equals(players[i].getNickname())) listeners[i] = null;
        }
    }
    public int kick(String nick){
        for(int i=0; i<numOfPlayers;i++){
            if(nick.equals(players[i].getNickname())){
                listeners[i] = null;
                players[i] = null;
            }
        }
        int result = 0;
        for(int i=0; i<numOfPlayers; i++){
            if(players[i] != null) result++;
        }
        return result;
    }

    public int getNumOfPlayers(){
        return numOfPlayers;
    }

    public int getCurrentPlayer(){
        return currentPlayer;
    }

    public int getNumOfActivePlayers(){
        int result = 0;
        for(int i=0; i<numOfPlayers;i++) if(listeners[i] != null) result++;
        return result;
    }

    public GlobalGoal[] getGoals() throws CloneNotSupportedException {
        GlobalGoal[] temp = new GlobalGoal[this.goals.length];
        for( int i = 0; i < this.goals.length; i++ )
            temp[i] = goals[i].clone();
        return temp;
    }

    public void nextPlayer(){
        currentPlayer = (currentPlayer+1) % players.length;
        while(listeners[currentPlayer] == null) currentPlayer = (currentPlayer+1) % players.length;
    }

    public Player getPlayer(int p) throws InvalidIndexException{
        if( ( p < 0 ) || ( p >= players.length ) ){
            throw new InvalidIndexException();
        }
        return players[p];
    }

    public void checkGlobalGoals() throws EmptyStackException, InvalidScoreException, InvalidIndexException, MissingShelfException {
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
    }

    public TileSack getTileSack(){
        return sack;
    }

    public GameBoard getGameBoard(){
        return board;
    }

    public boolean refillGameBoard(){
        if( !this.board.toRefill() )
            return false;

        boolean noTileAdded = true;
        int remain[] = new int[TileColor.values().length];

        // For each Coordinate in the board
        for( Coordinates c : this.board.getCoords() ){
            // Check if the sack is empty
            if( ( Arrays.stream(this.sack.getRemaining()).sum() == 0 ) && noTileAdded )
                return false;
            else if( Arrays.stream(this.sack.getRemaining()).sum() == 0 )
                break;

            // Add a new Tile from the sack to the board
            try{
                if( this.board.getTile(c) == null ) {
                    this.board.setTile(c, this.sack.pop());
                    if( noTileAdded ) noTileAdded = false;
                }
            }
            catch( InvalidCoordinatesForCurrentGameException e ){
                e.printStackTrace();
            }
        }
        // at least one Tile was added
        return true;
    }

    private GlobalGoal[] pickTwoGlobalGoals() throws InvalidNumberOfPlayersException {
        List<GlobalGoal> goals = GlobalGoal.getOneForEachChild(players.length);

        Collections.shuffle(goals);
        GlobalGoal[] returned = new GlobalGoal[2];
        for( int i = 0; i < returned.length; i++ ){
            returned[i] = goals.get(i);
        }

        return returned;
    }

    public void addCheater(String nickname){
        cheaters.add(nickname);
        notifyAllListeners();
    }

    public Stack<String> getCheaters(){ return cheaters;}

    private void notifyAllListeners(){
        Message gv = new UpdateViewMessage(new GameView(this));
        for (GameListener el: listeners) {
            el.updateGame(gv);
        }
    }
}
