package it.polimi.ingsw.Network;

import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.View.View;

import java.rmi.RemoteException;

public class ClientImplementation implements Client {
    View view;
    Server server;

    public ClientImplementation(View view, Server server){
        this.view = view;
        this.server = server;
        view.addListener((message) -> {
            try {
                server.handleMessage(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void update(Message m) throws RemoteException {
        this.view.notifyListeners(m);
    }


}
