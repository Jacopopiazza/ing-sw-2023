package it.polimi.ingsw.Network.Middleware;

import it.polimi.ingsw.Client.AppClientImplementation;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.logging.Level;

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

        } catch (IOException e) {
            throw new RemoteException("Unable to connect to the server", e);
        }

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

    public void close() throws RemoteException {
        try {
            socket.close();
        } catch (IOException e) {
            throw new RemoteException("Cannot close socket", e);
        }
    }

    public void handleMessage(Message m, Client client) throws RemoteException {

        Message toBeSent = m;

        AppClientImplementation.logger.log(Level.INFO, "ServerStub is sending message to server");

        if( m instanceof ReconnectMessage ){
            toBeSent = new ReconnectMessageTicket(((ReconnectMessage) m).getUsername() );
        }
        else if( m instanceof RegisterMessage){
            toBeSent = new RegisterMessageTicket(((RegisterMessage) m).getUsername(), ((RegisterMessage) m).getNumOfPlayers() );
        }
        try {
            oos.writeObject(toBeSent);
        } catch (IOException e) {
            throw new RemoteException("Cannot send message", e);
        }
        AppClientImplementation.logger.log(Level.INFO, "ServerStub sent message to server");

    }

    public void receive(Client client) throws RemoteException {
        AppClientImplementation.logger.log(Level.INFO, "ServerStub is waiting for a message from server");

        Message m;
        try {
            m = (Message) ois.readObject();
        } catch (IOException e) {
            throw new RemoteException("ServerStub: Cannot receive model view from server", e);
        } catch (ClassNotFoundException e) {
            throw new RemoteException("ServerStub: Cannot deserialize model view from server", e);
        }
        AppClientImplementation.logger.log(Level.INFO, "Server stub received message: " + m.toString());

        if( m instanceof GameServerMessageTicket)
            client.update(new GameServerMessage(this));
        else client.update(m);

    }


}
