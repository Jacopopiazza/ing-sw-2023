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
    private Map<String, Controller> playingNicknames;
    private Map<String, Controller> disconnectedNicknames;
    private Queue<Controller> lobbiesWaitingToStart;

    //numOfPlayers is 1 when the player wants to join a lobby, otherwise is the numOfPlayers for the new lobby
    private void register(GameListener listener, String nick, int numOfPlayers){
        if(numOfPlayers<=0 || numOfPlayers>4){
            listener.update(new InvalidNumOfPlayersMessage());
            return;
        }
        if(nick == null){
            listener.update(new MissingUsernameMessage());
            return;
        }
        synchronized (playingNicknames){
            synchronized (disconnectedNicknames){
                if(playingNicknames.containsKey(nick) || disconnectedNicknames.containsKey(nick)){
                    listener.update(new TakenUsernameMessage());
                    return;
                }
            }
            synchronized (lobbiesWaitingToStart){
                if(numOfPlayers != 1){
                    Controller lobby = new Controller(numOfPlayers,this);
                    lobby.addPlayer(nick,listener);
                    playingNicknames.put(nick,lobby);
                        lobbiesWaitingToStart.add(lobby);
                }
                else{
                    if(lobbiesWaitingToStart.peek() != null){
                        if(lobbiesWaitingToStart.peek().addPlayer(nick,listener)) {
                            lobbiesWaitingToStart.poll();
                            while(lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbiesWaitingToStart.poll();
                        }
                    }
                    else listener.update(new NoLobbyAvailableMessage());
                }
            }
        }
    }

    private void reconnect(String nick, GameListener listener){
        synchronized (playingNicknames){
            synchronized (disconnectedNicknames){
                if( !(disconnectedNicknames.containsKey(nick)) ){
                    listener.update(new NoUsernameToReconnectMessage());
                    return;
                }
                playingNicknames.put(nick,disconnectedNicknames.get(nick));
                disconnectedNicknames.remove(nick);
                playingNicknames.get(nick).reconnect(nick,listener);
            }
        }
    }

    // when Client does not ping back and when the user quits the lobby or the game
    private void disconnect(String nick){
        synchronized (playingNicknames){
            if( !playingNicknames.containsKey(nick) ) return;
            Controller controller = playingNicknames.get(nick);
            playingNicknames.remove(nick);
            if(controller.isGameStarted()){
                synchronized (disconnectedNicknames){
                    disconnectedNicknames.put(nick,controller);
                    controller.disconnect(nick);
                }
            }
            else{
                controller.kick(nick);
                if(controller == lobbiesWaitingToStart.peek() && controller.getNumOfActivePlayers() == 0){
                    lobbiesWaitingToStart.poll();
                    while(lobbiesWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbiesWaitingToStart.poll();
                }
            }
        }
    }

}
