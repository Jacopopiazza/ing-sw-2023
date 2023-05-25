package it.polimi.ingsw.Client;


import it.polimi.ingsw.View.TextualUI;
import it.polimi.ingsw.View.View;

import java.io.IOException;
import java.util.logging.*;

public class AppClientImplementation {

    public static final Logger logger = Logger.getLogger(AppClientImplementation.class.getName());

    public static void main(String[] args) {

        setUpLogger();

        View ui;

        if(args.length > 0 && args[0].equals("cli"))
            ui = new TextualUI();
        else{
            //ui = new GraphicalUI();
            throw new RuntimeException("Graphical UI not implemented yet");
        }

        ui.run();


    }

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

        // Crea un gestore di log sulla console
        ConsoleHandler consoleHandler = new ConsoleHandler();

        // Imposta il livello di logging dei gestori
        fileHandler.setLevel(Level.ALL);
        consoleHandler.setLevel(Level.ALL);

        // Aggiungi i gestori al logger
        logger.addHandler(fileHandler);
        //logger.addHandler(consoleHandler);

    }



}
