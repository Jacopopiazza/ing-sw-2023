package it.polimi.ingsw.Network.Middleware;

import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Network.Client;
import it.polimi.ingsw.Network.ClientImplementation;
import it.polimi.ingsw.Network.Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.logging.Level;

/**
 * The ServerStub class represents a client-side stub for the server.
 * It is responsible for establishing a connection to the server, sending and receiving messages.
 */
public class ServerStub implements Server {
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Socket socket;

    /**
     * Constructs a ServerStub object with the specified IP address and port number.
     *
     * @param ip   the IP address of the server
     * @param port the port number of the server
     * @throws RemoteException if an error occurs while connecting to the server
     */
    public ServerStub(String ip, int port) throws RemoteException {
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

    /**
     * Sends a {@code Message} to the server.
     *
     * @param m      the {@link Message} to be sent
     * @param client the {@link Client} object that sends the message
     * @throws RemoteException if an error occurs while sending the message
     */
    public void handleMessage(Message m, Client client) throws RemoteException {

        ClientImplementation.logger.log(Level.INFO, "ServerStub is sending message to server");

        try {
            oos.writeObject(m);
        } catch (IOException e) {
            throw new RemoteException("Cannot send message", e);
        }
        ClientImplementation.logger.log(Level.INFO, "ServerStub sent message to server");

    }

    /**
     * Receives a {@code Message} from the server.
     *
     * @param client the {@link Client} object that receives the message
     * @throws RemoteException if an error occurs while receiving the message
     */
    public void receive(Client client) throws RemoteException {
        ClientImplementation.logger.log(Level.INFO, "ServerStub is waiting for a message from server");

        Message m;
        try {
            m = (Message) ois.readObject();
        } catch (IOException e) {
            throw new RemoteException("ServerStub: Cannot receive model view from server", e);
        } catch (ClassNotFoundException e) {
            throw new RemoteException("ServerStub: Cannot deserialize model view from server", e);
        }
        ClientImplementation.logger.log(Level.INFO, "Server stub received message: " + m.toString());

        if( m instanceof GameServerMessageTicket)
            client.update(new GameServerMessage(this));
        else
            client.update(m);
    }


}
