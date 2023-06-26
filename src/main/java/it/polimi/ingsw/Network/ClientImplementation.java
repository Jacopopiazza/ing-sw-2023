package it.polimi.ingsw.Network;

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
    private View view;
    private Server server;
    public static final Logger logger = Logger.getLogger(ClientImplementation.class.getName());

    /**
     * Constructs a ClientImplementation instance with the specified view and server.
     *
     * @param view   the view associated with the client
     * @param server the server handling the client's messages
     * @throws RemoteException if a remote communication error occurs
     */
    public ClientImplementation(View view, Server server) throws RemoteException{
        super();
        this.view = view;
        this.server = server;
        view.addListener((message) -> {
            try {
                server.handleMessage(message, (Client)this);
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
                System.err.println(e.getCause());
                return;
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
            try{
                this.server.handleMessage(new PingMessage(((PingMessage) m).getpingNumber()), this);
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
                server.handleMessage(message, (Client)this);
            } catch (RemoteException e) {
                System.err.println(e.getMessage());
                System.err.println(e.getCause());
                return;
            }


        });

    }


    /**
     * The main entry point of the client application.
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) {

        setUpLogger();

        View ui;

        if(args.length > 0 && args[0].equals("cli"))
            ui = new TextualUI();
        else{
            ui = new GraphicalUI();
        }

        ui.run();


    }

    /**
     * Sets up the logger for the client application.
     * Configures the logger level, creates log file handler and console handler,
     * and adds the handlers to the logger.
     */
    private static void setUpLogger(){
        logger.setLevel(Level.ALL); // Imposta il livello di logging desiderato

        FileHandler fileHandler;

        // Crea un gestore di log su file
        try{
            fileHandler = new FileHandler("client.log");
        }catch (IOException ex){
            System.err.println("Cannot create log file. Halting...");
            return;
        }
        fileHandler.setFormatter(new SimpleFormatter());


        // Imposta il livello di logging dei gestori
        fileHandler.setLevel(Level.ALL);

        // Aggiungi i gestori al logger
        logger.addHandler(fileHandler);

    }


}
