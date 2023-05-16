package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Coordinates;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// RMI      -> ??
// SOCKET   -> ??
public class GameServer implements Server{
    private Controller controller;
    private  ServerImplementation serverImplementation = null;
    private List<String> playingUsernames;
    private List<String> disconnectedUsernames;

    public GameServer(ServerImplementation serverImplementation, int numOfPlayers) throws RemoteException {
        this.serverImplementation = ServerImplementation.getInstance();
        this.controller = new Controller(numOfPlayers, this);
        this.playingUsernames = new ArrayList<>();
        this.disconnectedUsernames = new ArrayList<>();
    }

    public boolean isGameStarted(){
        return this.controller.isGameStarted();
    }

    // when Client does not ping back and when the user quits the lobby or the game
    private void disconnect(String username){
        synchronized (playingUsernames){
            if( !playingUsernames.contains(username) ) return;

            playingUsernames.remove(username);

            if(isGameStarted()){
                synchronized (disconnectedUsernames){
                    disconnectedUsernames.add(username);
                    controller.disconnect(username);
                }
                serverImplementation.disconnect(username, this);
            } else {
                controller.kick(username);
                serverImplementation.kick(username, this);
            }

        }
    }

    private void doTurn(String username, Coordinates[] chosenTiles, int column){
        synchronized (playingUsernames){
            if( !playingUsernames.contains(username) || !isGameStarted()) return; // message is ignored
            controller.doTurn(username,chosenTiles,column);
        }
    }

    public void deleteGame(List<String> players){
        synchronized (playingUsernames){
            synchronized (disconnectedUsernames){
                for(String player: players){
                    playingUsernames.remove(player);
                    disconnectedUsernames.remove(player);
                }
            }
        }
    }

    public void reconnect(String username, GameListener listener){
        this.controller.reconnect(username, listener);
    }

    public boolean addPlayer(String username, GameListener listener){
        return this.controller.addPlayer(username, listener);
    }

    public int getNumOfActivePlayers(){
        return controller.getNumOfActivePlayers();
    }

    // Has to handle all the messages
    @Override
    public void handleMessage(Message m) throws RemoteException {
        if(m instanceof TurnActionMessage){

            TurnActionMessage message = (TurnActionMessage) m;
            doTurn(message.getUsername(),message.getChosenTiles(),message.getColumn());

        }
        else if(m instanceof DisconnectMessage){
            DisconnectMessage message = (DisconnectMessage) m;
            disconnect(message.getUsername());
        }
        else{
            System.err.println("Message not recognized; ignoring it");
        }
    }
}
