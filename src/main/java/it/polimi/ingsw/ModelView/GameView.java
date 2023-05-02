package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.*;

import java.io.Serializable;
import java.util.EventListener;

public class GameView implements Serializable {

    private final GameBoardView board;
    private final int numOfPlayers;
    private final PlayerView[] players;
    //private final GlobalGoal[] goals;
    private final int currentPlayer;
    private final TileSackView sack;

    //private final Exception exception;

    public GameView(Game game){
        this.board = new GameBoardView(game.getGameBoard());
        this.numOfPlayers = game.getNumOfPlayers();
        this.players = new PlayerView[this.numOfPlayers];
        for(int i = 0;i < this.numOfPlayers; i++){
            this.players[i] = new PlayerView(game.getPlayer(i));
        }
        this.currentPlayer = game.getCurrentPlayer();
        this.sack = new TileSackView(game.getTileSack());
    }

    public GameBoardView getBoard() {
        return board;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public PlayerView[] getPlayers() {
        return players;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public TileSackView getSack() {
        return sack;
    }
}
