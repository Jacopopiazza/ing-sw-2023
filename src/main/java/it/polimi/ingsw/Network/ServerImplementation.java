package it.polimi.ingsw.Network;

import it.polimi.ingsw.Controller.Controller;
import it.polimi.ingsw.Listener.GameListener;
import it.polimi.ingsw.Messages.InvalidNumOfPlayersMessage;
import it.polimi.ingsw.Messages.NoLobbyAvailableMessage;
import it.polimi.ingsw.Messages.NoUsernameToReconnectMessage;
import it.polimi.ingsw.Messages.TakenUsernameMessage;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class ServerImplementation implements Server{
    private ExecutorService executorService;
    private Map<String, Controller> playingNicknames;
    private Map<String, Controller> disconnectedNicknames;
    private Queue<Controller> lobbyWaitingToStart;

    //numOfPlayers is 1 when the player wants to join a lobby, otherwise is the numOfPlayers for the new lobby
    private void register(GameListener listener, String nick, int numOfPlayers){
        if(numOfPlayers<=0 || numOfPlayers>4){
            listener.update(new InvalidNumOfPlayersMessage());
            return;
        }
        synchronized (playingNicknames){
            synchronized (disconnectedNicknames){
                if(playingNicknames.containsKey(nick) || disconnectedNicknames.containsKey(nick)){
                    listener.update(new TakenUsernameMessage());
                    return;
                }
            }
            synchronized (lobbyWaitingToStart){
                if(numOfPlayers != 1){
                    Controller lobby = new Controller(numOfPlayers,this);
                    lobby.addPlayer(nick,listener);
                    playingNicknames.put(nick,lobby);
                        lobbyWaitingToStart.add(lobby);
                }
                else{
                    if(lobbyWaitingToStart.peek() != null){
                        if(lobbyWaitingToStart.peek().addPlayer(nick,listener)) {
                            lobbyWaitingToStart.poll();
                            while(lobbyWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbyWaitingToStart.poll();
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

    private void quit (String nick){
        synchronized (playingNicknames){
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
                if(controller == lobbyWaitingToStart.peek() && controller.getNumOfActivePlayers() == 0){
                    lobbyWaitingToStart.poll();
                    while(lobbyWaitingToStart.peek().getNumOfActivePlayers() == 0) lobbyWaitingToStart.poll();
                }
            }
        }
    }

}
