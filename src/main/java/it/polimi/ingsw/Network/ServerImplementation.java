package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.InvalidNumOfPlayersMessage;
import it.polimi.ingsw.Messages.NoLobbyAvailableMessage;
import it.polimi.ingsw.Messages.NoUsernameToReconnectMessage;
import it.polimi.ingsw.Messages.TakenUsernameMessage;
import it.polimi.ingsw.Messages.MissingUsernameMessage;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class ServerImplementation implements Server{
    private ExecutorService executorService;
    private Map<String, Controller> playingUsernames;
    private Map<String, Controller> disconnectedUsernames;
    private Queue<Controller> lobbiesWaitingToStart;

    //numOfPlayers is 1 when the player wants to join a lobby, otherwise is the numOfPlayers for the new lobby
    private void register(GameListener listener, String username, int numOfPlayers){
        if(numOfPlayers<=0 || numOfPlayers>4){
            listener.update(new InvalidNumOfPlayersMessage());
            return;
        }
        if(username == null){
            listener.update(new MissingUsernameMessage());
            return;
        }
        synchronized (playingUsernames){
            synchronized (disconnectedUsernames){
                if(playingUsernames.containsKey(username) || disconnectedUsernames.containsKey(username)){
                    listener.update(new TakenUsernameMessage());
                    return;
                }
            }
            synchronized (lobbiesWaitingToStart){
                if(numOfPlayers != 1){
                    Controller lobby = new Controller(numOfPlayers,this);
                    lobby.addPlayer(username,listener);
                    playingUsernames.put(username,lobby);
                        lobbiesWaitingToStart.add(lobby);
                }
                else{
                    if(lobbiesWaitingToStart.peek() != null){
                        if(lobbiesWaitingToStart.peek().addPlayer(username,listener)) {
                            lobbiesWaitingToStart.poll();
                            while(lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbiesWaitingToStart.poll();
                        }
                    }
                    else listener.update(new NoLobbyAvailableMessage());
                }
            }
        }
    }

    private void reconnect(String username, GameListener listener){
        synchronized (playingUsernames){
            synchronized (disconnectedUsernames){
                if( !(disconnectedUsernames.containsKey(username)) ){
                    listener.update(new NoUsernameToReconnectMessage());
                    return;
                }
                playingUsernames.put(username,disconnectedUsernames.get(username));
                disconnectedUsernames.remove(username);
                playingUsernames.get(username).reconnect(username,listener);
            }
        }
    }

    // when Client does not ping back and when the user quits the lobby or the game
    private void disconnect(String username){
        synchronized (playingUsernames){
            if( !playingUsernames.containsKey(username) ) return;
            Controller controller = playingUsernames.get(username);
            playingUsernames.remove(username);
            if(controller.isGameStarted()){
                synchronized (disconnectedUsernames){
                    disconnectedUsernames.put(username,controller);
                    controller.disconnect(username);
                }
            }
            else{
                controller.kick(username);
                if(controller == lobbiesWaitingToStart.peek() && controller.getNumOfActivePlayers() == 0){
                    lobbiesWaitingToStart.poll();
                    while(lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbiesWaitingToStart.poll();
                }
            }
        }
    }

}
