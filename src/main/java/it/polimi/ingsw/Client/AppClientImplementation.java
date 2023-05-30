package it.polimi.ingsw.Client;


import it.polimi.ingsw.View.GraphicalUI;
import it.polimi.ingsw.View.TextualUI;
import it.polimi.ingsw.View.View;

import java.io.IOException;
import java.util.logging.*;


/**
 * The main client application responsible for starting the client user interface.
 */
public class AppClientImplementation {

    public static final Logger logger = Logger.getLogger(AppClientImplementation.class.getName());

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
