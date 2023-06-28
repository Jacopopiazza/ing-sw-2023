package it.polimi.ingsw.Network;

import it.polimi.ingsw.Exceptions.SingletonException;
import it.polimi.ingsw.Messages.GameServerMessage;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.PingMessage;
import it.polimi.ingsw.View.GraphicalUI;
import it.polimi.ingsw.View.TextualUI;
import it.polimi.ingsw.View.View;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * The ClientImplementation class is an implementation of the Client interface.
 */
public class ClientImplementation extends UnicastRemoteObject implements Client {
    private static ClientImplementation instance;
    public static final Logger logger = Logger.getLogger(ClientImplementation.class.getName());
    private View view;
    private Server server;

    /**
     * Constructs a ClientImplementation instance with the specified view and server.
     *
     * @param view   the view associated with the client
     * @param server the server handling the client's messages
     * @throws RemoteException if a remote communication error occurs
     */

    public static ClientImplementation getInstance(View view, Server server) throws SingletonException, RemoteException {
        if( instance == null )
            instance = new ClientImplementation(view, server);
        if( ( instance.view != view ) || ( instance.server != server ) )
            throw new SingletonException();
        return instance;
    }

    /**
     * The main entry point of the client application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {
        setUpLogger();
        View ui = ( ( args.length>0 ) && args[0].equals("cli") ) ? new TextualUI() : new GraphicalUI();
        ui.run();
    }

    /**
     * Sets up the logger for the client application.
     * Configures the logger level, creates log file handler and console handler,
     * and adds the handlers to the logger.
     */
    private static void setUpLogger(){
        logger.setLevel(Level.ALL);

        FileHandler fileHandler;

        try {
            fileHandler = new FileHandler("client.log");
        } catch (IOException ex) {
            System.err.println("Cannot create log file. Halting...");
            return;
        }
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.SEVERE);
        logger.addHandler(fileHandler);
    }

    private ClientImplementation(View view, Server server) throws RemoteException{
        super();
        this.view = view;
        this.server = server;
        view.addListener((message) -> {
            try {
                server.handleMessage(message, this);
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
                System.err.println(e.getCause());
            }

        });

    }

    /**
     * Updates the client with the specified message.
     *
     * @param m the message to update the client with
     * @throws RemoteException if a remote communication error occurs
     */

    @Override
    public void update(Message m) throws RemoteException {

        if(m instanceof GameServerMessage){
            changeServer(((GameServerMessage) m).getServer());
            return;
        }


        if(m instanceof PingMessage){
            ClientImplementation.logger.log(Level.INFO, "Ping #" + ((PingMessage) m).getPingNumber() + " received");
            try{
                this.server.handleMessage(new PingMessage(((PingMessage) m).getPingNumber()), this);
            }catch (RemoteException e) {
                System.err.println(e.getMessage());
                System.err.println(e.getCause());
                return;
            }
            return;
        }

        this.view.update(m);
    }

    private void changeServer(Server server){
        this.server = server;

        view.clearListeners();

        view.addListener((message) -> {
            try {
                server.handleMessage(message, this);
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
                System.err.println(e.getCause());
            }
        });

    }

}
