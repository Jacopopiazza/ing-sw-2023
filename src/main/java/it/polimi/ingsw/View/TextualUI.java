package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Messages.GameServerMessage;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Messages.NoUsernameToReconnectMessage;
import it.polimi.ingsw.Messages.TakenUsernameMessage;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Utilities.ConsoleColors;
import it.polimi.ingsw.ModelView.GameBoardView;
import it.polimi.ingsw.ModelView.GameView;
import it.polimi.ingsw.ModelView.ShelfView;
import it.polimi.ingsw.ModelView.TileView;


import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.util.Set;


public class TextualUI extends ClientManager {

    private Scanner in;
    private PrintStream out;

    private String userName;

    // Write this title in a config file
    String r1 = " __    __           ______ _           _  __  _";
    String r2 = "|  \\  /  |         /  ____| |         | |/ _|(_)";
    String r3 = "| \\ \\/ / |_   _   |  (___ | |__   ___ | | |_  _  ___";
    String r4 = "| |\\__/| | | | |   \\___  \\|  _ \\ / _ \\| |  _|| |/ _ \\";
    String r5 = "| |    | | |_| |    ___)  | | | |  __/| | |  | |  __/";
    String r6 = "|_|    |_|\\__, |   |_____/|_| |_|\\___/|_|_|  |_|\\___/";
    String r7 = "            _/ |";
    String r8 = "           |__/";
    int padding;

    String user = null;
    GameBoard gameBoard;

    // Needs the view

    public TextualUI() {
        super();
        in = new Scanner(System.in);
        out = new PrintStream(System.out, true);
    }

    private void showTitle(){
        out.println(r1);
        out.println(r2);
        out.println(r3);
        out.println(r4);
        out.println(r5);
        out.println(r6);
        out.println(r7);
        out.println(r8);
    }

    public boolean chooseConnection(){
        int choice = -1;
        boolean validConnection = false;

        out.println("Choose the connection type:");
        out.println("1 - RMI");
        out.println("2 - SOCKET\n");

        do {
            if (in.hasNextInt()) {
                choice = in.nextInt();
                in.nextLine();

                if (choice >= 0 && choice <= 1) {
                    validConnection = true;
                } else {
                    out.println("Invalid selection!");
                }
            } else {
                in.nextLine();
                out.println("Invalid integer provided!");
            }
        } while (!validConnection);

        if(choice == 1){
            out.println("Connecting with RMI...");
            try{
                this.setUpRMIClient();
            }catch (RemoteException | NotBoundException ex ){
                out.println("Cannot connect with RMI. Trying with socket...");
                return false;
            }
        }else{
            out.println("Connecting with socket...");
            out.println("Connecting with RMI...");
            try{
                this.setUpSocketClient();
            }catch (RemoteException | NotBoundException ex ){
                out.println("Cannot connect with RMI. Trying with socket...");
                return false;
            }
        }
        return true;
    }

    private void readUsername(){
        out.println("Insert username:");
        Scanner inputScanner = new Scanner(System.in);
        this.userName = inputScanner.nextLine();
    }

    public void initializePlayer(String username){
        // Send the Reconnect
        //if i can't -> send the register
    }

    private String getColorCode(TileView tile){
        TileColor tc = tile.getCOLOR();
        switch(tc) {
            case WHITE: {
                return ConsoleColors.WHITE_BACKGROUND_BRIGHT.getCode();
            }
            case FUCHSIA:{
                return ConsoleColors.PURPLE_BACKGROUND_BRIGHT.getCode();
            }
            case BLUE:{
                return ConsoleColors.BLUE_BACKGROUND_BRIGHT.getCode();
            }
            case CYAN:{
                return ConsoleColors.CYAN_BACKGROUND_BRIGHT.getCode();
            }
            case GREEN:{
                return ConsoleColors.GREEN_BACKGROUND_BRIGHT.getCode();
            }
            case YELLOW:{
                return ConsoleColors.YELLOW_BACKGROUND.getCode();
            }
        }
        return ConsoleColors.RESET.getCode();
    }

    public void showBoard(){
        // print the board -> using a 4 player board for a first try
        Game game = new Game(4);// Instanciated just for try
        game.addPlayer("a", (message -> {out.println("ciao");}));
        game.addPlayer("d", (message -> {out.println("ciao");}));
        game.addPlayer("b", (message -> {out.println("ciao");}));
        game.addPlayer("c", (message -> {out.println("ciao");}));
        game.init();

        try {
            game.refillGameBoard();

        }catch (Exception e){

        }

        GameView modelView = new GameView(game);

        GameBoardView gameBoard = modelView.getGameBoard();

        int max_x;
        int max_y;

        max_x = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).max().getAsInt();
        max_y = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).max().getAsInt();

        String color = ConsoleColors.RESET.getCode();
        TileColor tc = TileColor.WHITE;

        Set<Coordinates> coords = gameBoard.getCoords();

        out.print("     ");
        for(int c = 0; c < max_y; c++){
            out.print(" " + c + "   ");
        }
        out.print("\n\n");

        for(int r = 0; r < max_x; r++){
            out.print(" " + (char)(r + 65) + "   ");
            for(int c = 0; c < max_y; c++){
                if(coords.contains(new Coordinates(r, c)) && gameBoard.getTile(new Coordinates(r,c)) != null){
                    color = getColorCode(gameBoard.getTile(new Coordinates(r, c)));
                }else{
                    color = ConsoleColors.RESET.getCode();
                }
                out.print(color + "   " + ConsoleColors.RESET.getCode() + "  ");
            }
            out.print(ConsoleColors.RESET.getCode() + "");
            out.print("\n\n");
        }
    }

    public void showShelf(ShelfView shelf){
        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        String color = ConsoleColors.RESET.getCode();
        TileView tile = null;

        for(int i = 0; i < c; i++)
            out.print(" " + i + "   ");
        out.print("\n\n");

        for(int i = 0; i < r; i++){
            for(int k = 0; k < c; k++){
                tile = shelf.getTile(new Coordinates(i, k));
                if(tile != null){
                    color = getColorCode(tile);
                }else{
                    color = ConsoleColors.BROWN_BACKGROUND.getCode();
                }
                out.print(color + "   " + ConsoleColors.RESET.getCode() + "  ");
            }
            out.print(ConsoleColors.RESET.getCode() + "");
            out.print("\n\n");
        }
    }

    public void show() {
        boolean connected;

        showTitle();
        connected = chooseConnection();
        if(!connected){
            return;
        }
        out.println("Connected!");
        readUsername();

        out.println("Your username is: " + this.userName);

        out.println("Trying to connected to server...");
        doReconnect(this.userName);
    }

    private void askPlayerNumOfPlayerForLobby(String username){
        out.println("Insert the number of players for the game:");
        int numOfPlayers = -1;

        do{
            numOfPlayers=in.nextInt();
        } while (numOfPlayers < 2 || numOfPlayers > 4);

        doConnect(this.userName, numOfPlayers);
    }

    @Override
    public void update(Message m){
        out.println("CLI Received message");

        if(m instanceof NoUsernameToReconnectMessage){
            out.println(m.toString());
            askPlayerNumOfPlayerForLobby(this.userName);
        }
        else if(m instanceof TakenUsernameMessage){
            out.println(m.toString());
            readUsername();
            askPlayerNumOfPlayerForLobby(this.userName);
        }
        else if(m instanceof GameServerMessage){
            out.println(m.toString());
            cleanListeners();
            addListener((message) -> {
                try {
                    ((GameServerMessage) m).getServer().handleMessage(message);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            });
        }
        else{
            out.println("Invalid message received; ignoring it.");
        }

    }

    @Override
    public void run() {
        show();
    }
}