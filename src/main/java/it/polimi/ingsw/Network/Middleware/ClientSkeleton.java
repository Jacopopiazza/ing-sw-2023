package it.polimi.ingsw.Network.Middleware;

import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.Server;
import it.polimi.ingsw.Network.ServerImplementation;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.logging.Level;

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
        System.out.println("ClientSkeleton is sending message to client");
        ServerImplementation.logger.log(Level.INFO, "ClientSkeleton is sending " + m.toString() + " message.");
        if( m instanceof GameServerMessage){
            server = ((GameServerMessage) m).getServer();
            m = new GameServerMessageTicket();
        }
        try {
            oos.writeObject(m);
        } catch (IOException e) {
            throw new RemoteException("Cannot send message", e);
        }

        ServerImplementation.logger.log(Level.INFO, "ClientSkeleton sent " + m.toString() + " message succesfully.");

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

        System.out.println("Received message: " + m.toString());

        if( m instanceof ReconnectMessageTicket ){
            Message packed = new ReconnectMessage(((ReconnectMessageTicket) m).getUsername());
            server.handleMessage(packed, this);
        }
        else if( m instanceof RegisterMessageTicket ){
            server.handleMessage(new RegisterMessage(((RegisterMessageTicket) m).getUsername(), ((RegisterMessageTicket) m).getNumOfPlayers()), this);
        }
        else server.handleMessage(m, this);
    }


}
