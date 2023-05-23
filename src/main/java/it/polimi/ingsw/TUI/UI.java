package it.polimi.ingsw.TUI;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import it.polimi.ingsw.Exceptions.EmptySackException;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.NoTileException;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Utilities.ConsoleColors;
import it.polimi.ingsw.ModelView.GameBoardView;
import it.polimi.ingsw.ModelView.GameView;
import it.polimi.ingsw.ModelView.ShelfView;
import it.polimi.ingsw.ModelView.TileView;


import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import static it.polimi.ingsw.Model.Utilities.ConsoleColors.WHITE;


public class UI {
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

    public UI() {

    }

    private void showTitle(){
        System.out.println(r1);
        System.out.println(r2);
        System.out.println(r3);
        System.out.println(r4);
        System.out.println(r5);
        System.out.println(r6);
        System.out.println(r7);
        System.out.println(r8);
    }

    public boolean chooseConnection(){
        Scanner scanner = new Scanner(System.in);
        String choiceStr;
        int choice;

        while(true){
            System.out.println("1 - RMI");
            System.out.println("2 - SOCKET\n");
            choiceStr = scanner.nextLine();
            try{
                choice = Integer.parseInt(choiceStr);
            } catch (NumberFormatException e) {
                continue;
            }

            if(choice == 1 || choice == 2){
                break;
            }
        }

        if(choice == 1){
            System.out.println("Connecting with RMI...");
        }else{
            System.out.println("Connecting with socket...");
        }
        return true;
    }

    private String readUsername(){
        System.out.println("Insert username:");
        Scanner inputScanner = new Scanner(System.in);
        String username = inputScanner.nextLine();
        return username;
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
        game.addPlayer("a", (message -> {System.out.println("ciao");}));
        game.addPlayer("d", (message -> {System.out.println("ciao");}));
        game.addPlayer("b", (message -> {System.out.println("ciao");}));
        game.addPlayer("c", (message -> {System.out.println("ciao");}));
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

        System.out.print("     ");
        for(int c = 0; c < max_y; c++){
            System.out.print(" " + c + "   ");
        }
        System.out.print("\n\n");

        for(int r = 0; r < max_x; r++){
            System.out.print(" " + (char)(r + 65) + "   ");
            for(int c = 0; c < max_y; c++){
                if(coords.contains(new Coordinates(r, c)) && gameBoard.getTile(new Coordinates(r,c)) != null){
                    color = getColorCode(gameBoard.getTile(new Coordinates(r, c)));
                }else{
                    color = ConsoleColors.RESET.getCode();
                }
                System.out.print(color + "   " + ConsoleColors.RESET.getCode() + "  ");
            }
            System.out.print(ConsoleColors.RESET.getCode() + "");
            System.out.print("\n\n");
        }
    }

    public void showShelf(ShelfView shelf){
        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        String color = ConsoleColors.RESET.getCode();
        TileView tile = null;

        for(int i = 0; i < c; i++)
            System.out.print(" " + i + "   ");
        System.out.print("\n\n");

        for(int i = 0; i < r; i++){
            for(int k = 0; k < c; k++){
                tile = shelf.getTile(new Coordinates(i, k));
                if(tile != null){
                    color = getColorCode(tile);
                }else{
                    color = ConsoleColors.BROWN_BACKGROUND.getCode();
                }
                System.out.print(color + "   " + ConsoleColors.RESET.getCode() + "  ");
            }
            System.out.print(ConsoleColors.RESET.getCode() + "");
            System.out.print("\n\n");
        }
    }

    public void show() {
        boolean connected;

        showTitle();
        connected = chooseConnection();
        if(!connected)
            System.out.println("Error during connection!");
        System.out.println("Connected!");
        user = readUsername();
        System.out.println("Your username is: " + user);

        System.out.print("\n\n\n\n\t\tGame Board");
        showBoard();

        Shelf shelf = new Shelf();

        try {
            shelf.addTile(new Tile(TileColor.CYAN, 0), 3);
            shelf.addTile(new Tile(TileColor.YELLOW, 0), 3);
        } catch (IllegalColumnInsertionException e) {
            e.printStackTrace();
        } catch (NoTileException e) {
            e.printStackTrace();
        }

        ShelfView shelfView = new ShelfView(shelf);

        System.out.print("\n\n\n\n\t\tMy Shelf\n\n\n");
        showShelf(shelfView);
    }
}