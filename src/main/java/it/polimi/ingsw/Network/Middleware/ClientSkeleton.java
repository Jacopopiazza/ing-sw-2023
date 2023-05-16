package it.polimi.ingsw.Network.Middleware;

import it.polimi.ingsw.Messages.ConnectToGameServerMessage;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.ReconnectMessage;
import it.polimi.ingsw.Messages.RegisterMessage;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

public class ClientSkeleton implements Client {
    private Server server;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    public ClientSkeleton(Server s, Socket socket) throws RemoteException {
        this.server = s;

        try {
            this.oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new RemoteException("Cannot create output stream", e);
        }
        try {
            this.ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RemoteException("Cannot create input stream", e);
        }
    }

    public void update(Message m) throws RemoteException {
        if( m instanceof ConnectToGameServerMessage ){
            server = ((ConnectToGameServerMessage) m).getServer();
            m = new ConnectToGameServerMessage(null);
        }
        try {
            oos.writeObject(m);
            oos.flush();
            oos.reset();
        } catch (IOException e) {
            throw new RemoteException("Cannot send message", e);
        }
    }

    public void receive() throws RemoteException {
        Message m;
        try {
            m = (Message) ois.readObject();
        } catch (IOException e) {
            throw new RemoteException("Cannot receive choice from client", e);
        } catch (ClassNotFoundException e) {
            throw new RemoteException("Cannot deserialize choice from client", e);
        }
        if( m instanceof ReconnectMessage ){
            server.handleMessage(new ReconnectMessage(((ReconnectMessage) m).getUsername(), this));
        }
        else if( m instanceof RegisterMessage ){
            server.handleMessage(new RegisterMessage(((RegisterMessage) m).getUsername(), this, ((RegisterMessage) m).getNumOfPlayers()));
        }
        else server.handleMessage(m);
    }


}
