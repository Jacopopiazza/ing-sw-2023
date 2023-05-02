package it.polimi.ingsw.network;

import it.polimi.ingsw.Controller.Controller;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class ServerImplementation implements Server{
    private ExecutorService executorService;
    private Map<String, Controller> playingNicknames;
    private Map<String, Controller> disconnectedNicknames;
    private Queue<Controller> lobbyWaitingToStart;

    //numOfPlayers is 1 when the player wants to join a lobby, otherwise is the numOfPlayers for the new lobby
    private void register(Client client, String nick, int numOfPlayers){
        if(numOfPlayers<=0 || numOfPlayers>4){
            //message with num of players not valid
            return;
        }
        synchronized (playingNicknames){
            synchronized (disconnectedNicknames){
                if(playingNicknames.containsKey(nick) || disconnectedNicknames.containsKey(nick)){
                    //message "nickname already taken"
                    return;
                }
            }
            synchronized (lobbyWaitingToStart){
                if(numOfPlayers != 1){
                    Controller lobby = new Controller(numOfPlayers,this);
                    lobby.addPlayer(nick,client);
                    playingNicknames.put(nick,lobby);
                        lobbyWaitingToStart.add(lobby);
                }
                else if(lobbyWaitingToStart.peek().addPlayer(nick,client)) lobbyWaitingToStart.poll();
            }
        }
    }
}
