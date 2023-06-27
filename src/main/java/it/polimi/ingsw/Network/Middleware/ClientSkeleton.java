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

/**
 * The client skeleton class represents a client's connection to the server. It implements the Client interface and
 * provides methods for sending and receiving messages to/from the server.
 */
public class ClientSkeleton implements Client {
    private Server server;
    private Socket socket;
    private final ObjectOutputStream oos;
    private final ObjectInputStream ois;

    /**
     * Constructs a new ClientSkeleton instance.
     *
     * @param s      the server instance
     * @param socket the socket representing the client's connection
     * @throws RemoteException if an error occurs during the creation of input/output streams
     */
    public ClientSkeleton(Server s, Socket socket) throws RemoteException {
        this.server = s;
        this.socket = socket;

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

    /**
     * Sends a message to the client.
     *
     * @param m the message to be sent
     * @throws RemoteException if an error occurs while sending the message
     */
    public void update(Message m) throws RemoteException {
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

        ServerImplementation.logger.log(Level.INFO, "ClientSkeleton sent " + m.toString() + " message successfully.");
    }

    /**
     * Closes the connection to the client.
     *
     * @throws RemoteException if an error occurs while closing the socket
     */
    public void close() throws RemoteException {
        try {
            this.socket.close();
        } catch (IOException e) {
            throw new RemoteException("Cannot close socket", e);
        }
    }

    /**
     * Receives a message from the client.
     *
     * @throws RemoteException if an error occurs while receiving the message
     */
    public void receive() throws RemoteException {
        Message m;
        try {
            m = (Message) ois.readObject();
        } catch (IOException e) {
            throw new RemoteException("Cannot receive choice from client", e);
        } catch (ClassNotFoundException e) {
            throw new RemoteException("Cannot deserialize choice from client", e);
        }

        server.handleMessage(m, this);
    }


}
