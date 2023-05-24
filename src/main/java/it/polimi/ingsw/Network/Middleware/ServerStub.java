package it.polimi.ingsw.Network.Middleware;

import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;

public class ServerStub implements Server {
    String ip;
    int port;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;

    public ServerStub(String ip, int port) throws RemoteException {
        this.ip = ip;
        this.port = port;

        try {
            this.socket = new Socket(ip, port);
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
        } catch (IOException e) {
            throw new RemoteException("Unable to connect to the server", e);
        }
    }


    public void handleMessage(Message m) throws RemoteException {
        if( m instanceof ReconnectMessage ){
            m = new ReconnectMessageTicket(((ReconnectMessage) m).getUsername() );
        }
        else if( m instanceof RegisterMessage){
            m = new RegisterMessageTicket(((RegisterMessage) m).getUsername(), ((RegisterMessage) m).getNumOfPlayers() );
        }
        try {
            oos.writeObject(m);
            oos.flush();
            oos.reset();
        } catch (IOException e) {
            throw new RemoteException("Cannot send message", e);
        }
    }

    public void receive(Client client) throws RemoteException {
        Message m;
        try {
            m = (Message) ois.readObject();
        } catch (IOException e) {
            throw new RemoteException("Cannot receive model view from client", e);
        } catch (ClassNotFoundException e) {
            throw new RemoteException("Cannot deserialize model view from client", e);
        }
        if( m instanceof GameServerMessageTicket)
            client.update(new GameServerMessage(this));
        else client.update(m);

    }


}
