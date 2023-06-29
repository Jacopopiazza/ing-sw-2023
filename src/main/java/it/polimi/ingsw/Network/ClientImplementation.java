package it.polimi.ingsw.Network;

import it.polimi.ingsw.Exceptions.SingletonException;
import it.polimi.ingsw.Messages.GameServerMessage;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.PingMessage;
import it.polimi.ingsw.Utilities.Config;
import it.polimi.ingsw.View.GraphicalUI;
import it.polimi.ingsw.View.TextualUI;
import it.polimi.ingsw.View.View;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.*;

/**
 * The ClientImplementation class is an implementation of the {@link Client} interface.
 * It extends the {@link UnicastRemoteObject} class and provides methods for updating the client with a {@link Message}.
 */
public class ClientImplementation extends UnicastRemoteObject implements Client {
    private static ClientImplementation instance;

    /**
     * Logger used to log Client actions.
     */
    public static final Logger logger = Logger.getLogger(ClientImplementation.class.getName());

    private final View view;
    private Timer timer = new Timer();
    private Server server;

    /**
     * Returns the Singleton instance of {@code ClientImplementation}.
     *
     * @param view   the {@link View} associated with the client
     * @param server the {@link Server} handling the client's messages
     * @throws RemoteException if a remote communication error occurs
     * @throws SingletonException if instance != null and view != instance.view or server != instance.server
     * @return returns the existing Singleton instance of {@code ClientImplementation} or the new created instance.
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
        View ui = ( ( args.length>0 ) && args[0].equals("-cli") ) ? new TextualUI() : new GraphicalUI();
        ui.run();
    }

    /**
     * Sets up the logger for the client application.
     * Configures the logger level, creates log file handler and console handler,
     * and adds the handlers to the logger.
     */
    private static void setUpLogger(){
        FileHandler fileHandler;
        LogManager.getLogManager().reset();
        logger.setLevel(java.util.logging.Level.OFF);

        try {
            fileHandler = new FileHandler("client.log");
        } catch (IOException ex) {
            System.err.println("Cannot create log file. Halting...");
            return;
        }
        fileHandler.setFormatter(new SimpleFormatter());
        fileHandler.setLevel(Level.INFO);
        logger.addHandler(fileHandler);
    }

    /**
     * Constructs a new {@code ClientImplementation} object with the specified {@code View} and {@code Server}.
     *
     * @param view   the {@link View} associated with the client
     * @param server the {@link Server} handling the client's messages
     * @throws RemoteException if a remote communication error occurs
     */
    private ClientImplementation(View view, Server server) throws RemoteException{
        super();
        this.view = view;
        this.server = server;
        view.addListener((message) -> {
            try {
                server.handleMessage(message, this);
            } catch (RemoteException e) {
                ClientImplementation.logger.log(Level.SEVERE, "RemoteException occurred while sending message to server");
            }

        });

    }

    /**
     * Updates the client with the specified {@code Message}.
     *
     * @param m the {@link Message} to update the client with
     * @throws RemoteException if a remote communication error occurs
     */

    @Override
    public void update(Message m) throws RemoteException {

        if(m instanceof GameServerMessage){
            changeServer(((GameServerMessage) m).getServer());
            return;
        }


        if(m instanceof PingMessage){

            timer.cancel();
            timer = new Timer();


            ClientImplementation.logger.log(Level.INFO, "Ping #" + ((PingMessage) m).getPingNumber() + " received");
            try{
                this.server.handleMessage(new PingMessage(((PingMessage) m).getPingNumber()), this);
            }catch (RemoteException e) {
                ClientImplementation.logger.log(Level.SEVERE, "RemoteException occurred while sending message to server");
                return;
            }
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    System.exit(0);
                }
            }, 1000 * Config.getInstance().getPingInterval() * 2);
            return;
        }

        this.view.update(m);
    }

    /**
     * Changes the {@code Server} handling the client's messages.
     *
     * @param server the new {@link Server} handling the client's messages
     */
    private void changeServer(Server server){
        this.server = server;

        view.clearListeners();

        view.addListener((message) -> {
            try {
                server.handleMessage(message, this);
            } catch (RemoteException e) {
                ClientImplementation.logger.log(Level.SEVERE, "RemoteException occurred while sending message to server");
            }
        });

    }

}
