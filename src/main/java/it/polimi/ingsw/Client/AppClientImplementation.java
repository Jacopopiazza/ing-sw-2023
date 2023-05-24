package it.polimi.ingsw.Client;

import it.polimi.ingsw.View.TextualUI;
import it.polimi.ingsw.View.View;

public class AppClientImplementation {

    public static void main(String[] args) {
        View ui;

        ui = new TextualUI();

        /*if(args.length > 0 && args[0].equals("cli"))
            ui = new TextualUI();
        else{
            //ui = new GraphicalUI();
            throw new RuntimeException("Graphical UI not implemented yet");
        }*/

        ui.run();


    }



}
