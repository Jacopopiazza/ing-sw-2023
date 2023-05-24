package it.polimi.ingsw.TUI;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.NoTileException;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Utilities.ConsoleColors;
import it.polimi.ingsw.ModelView.*;

import java.util.*;

// Todo: check the column insertion

public class TextualUI {
    // Write this title in a config file
    String r1 = " __    __           ______ _           _  __  _";
    String r2 = "|  \\  /  |         /  ____| |         | |/ _|(_)";
    String r3 = "| \\ \\/ / |_   _   |  (___ | |__   ___ | | |_  _  ___";
    String r4 = "| |\\__/| | | | |   \\___  \\|  _ \\ / _ \\| |  _|| |/ _ \\";
    String r5 = "| |    | | |_| |    ___)  | | | |  __/| | |  | |  __/";
    String r6 = "|_|    |_|\\__, |   |_____/|_| |_|\\___/|_|_|  |_|\\___/";
    String r7 = "            _/ |";
    String r8 = "           |__/";
    String user = null;

    // Needs the view

    public TextualUI() {

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

    private boolean checkUserInput(int lowerBound, int upperBound, int input){
        return input >= lowerBound && input <= upperBound;
    }

    private int readChoiceFromInput(String option_1, String option_2){
        Scanner scanner = new Scanner(System.in);
        String input;
        int choice;

        while(true){
            System.out.println("1 - " + option_1);
            System.out.println("2 - " + option_2 + "\n");
            input = scanner.nextLine();
            try{
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                continue;
            }

            if(checkUserInput(1,2, choice)){
                return choice;
            }
        }
    }

    public boolean chooseConnection(){
        int choice = readChoiceFromInput("RMI", "SOCKET");

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
        return inputScanner.nextLine();
    }

    public void initializePlayer(String username){
        // Send the Reconnect
        //if i can't -> send the register
    }

    private String getColorCode(TileView tile){
        TileColor tc = tile.getCOLOR();
        switch (tc) {
            case WHITE -> {
                return ConsoleColors.WHITE_BACKGROUND_BRIGHT.getCode();
            }
            case FUCHSIA -> {
                return ConsoleColors.PURPLE_BACKGROUND_BRIGHT.getCode();
            }
            case BLUE -> {
                return ConsoleColors.BLUE_BACKGROUND_BRIGHT.getCode();
            }
            case CYAN -> {
                return ConsoleColors.CYAN_BACKGROUND_BRIGHT.getCode();
            }
            case GREEN -> {
                return ConsoleColors.GREEN_BACKGROUND_BRIGHT.getCode();
            }
            case YELLOW -> {
                return ConsoleColors.YELLOW_BACKGROUND.getCode();
            }
        }
        return ConsoleColors.RESET.getCode();
    }

    public void showBoard(GameBoardView gameBoard){
        // print the board -> using a 4 player board for a first try
        int maxRow = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).max().getAsInt();
        int minRow = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).min().getAsInt();

        int maxCol = gameBoard.getCoords().stream().mapToInt(y -> y.getCOL()).max().getAsInt();
        int minCol = gameBoard.getCoords().stream().mapToInt(y -> y.getCOL()).min().getAsInt();

        String color;

        Set<Coordinates> coords = gameBoard.getCoords();

        System.out.println("\n\n\n\n\t\t\t\tGame Board");
        System.out.print("     ");

        for(int c = minCol; c < maxCol; c++){
            System.out.print(" " + (c - minCol) + "   ");
        }
        System.out.print("\n\n");

        for(int r = minRow; r < maxRow; r++){
            System.out.print(" " + (char)(r + 65 - minRow) + "   ");
            for(int c = minCol; c < maxCol; c++){
                if(coords.contains(new Coordinates(r, c)) && gameBoard.getTile(new Coordinates(r, c)) != null){
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

    private void printTile(TileView tileView){
        String color;
        if(tileView != null){
            color = getColorCode(tileView);
        }else{
            color = ConsoleColors.BROWN_BACKGROUND.getCode();
        }
        System.out.print(color + "   " + ConsoleColors.RESET.getCode() + "  ");
    }

    public void showPrivateGoals(Coordinates[] privateGoals){
        System.out.println("\n\n\tMy private goals\n\n");
        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        TileView tile = null;

        for(int i = 0; i < r; i++){
            for(int k = 0; k < c; k++){
                for(int color = 0; color < privateGoals.length; color++){
                    if(privateGoals[color].equals(new Coordinates(i, k))){
                        tile = new TileView(new Tile(TileColor.values()[color], 0));
                    }
                }
                printTile(tile);
                tile = null;
            }
            System.out.print(ConsoleColors.RESET.getCode() + "");
            System.out.print("\n\n");
        }
    }

    public void showShelf(ShelfView shelf){
        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        TileView tile;

        System.out.println("\n\n\n\n\t\tMy Shelf\n\n");
        for(int i = 0; i < c; i++)
            System.out.print(" " + i + "   ");
        System.out.print("\n\n");

        for(int i = 0; i < r; i++){
            for(int k = 0; k < c; k++){
                tile = shelf.getTile(new Coordinates(i, k));
                printTile(tile);
            }
            System.out.print(ConsoleColors.RESET.getCode() + "");
            System.out.print("\n\n");
        }
    }

    private List<Coordinates> pickTiles(int numRows, int numCols){
        /* Todo: check that the chosen Tiles from the board are actually pickable, on the same line, maximum 3
        *   -> controller*/
        List<Coordinates> coords = new ArrayList<>();

        int row, column, choice;

        Scanner scanner = new Scanner(System.in);
        String input;

        while(true) {
            choice = readChoiceFromInput("Pick a tile", "Done");
            if (choice == 1) {
                // Pick a tile
                while(true){
                    System.out.print("Insert the coordinates [ROW] [COLUMN]: ");
                    input = scanner.nextLine();
                    String[] c;
                    c = input.split("\\s+");    // split with one or multiple spaces
                    if( (c.length > 2) || (c.length < 2)
                        || c[0].length() > 1 || c[0].length() <= 0
                        || c[1].length() > 1 || c[1].length() <= 0
                        || !checkUserInput('A', numRows + 'A', c[0].toUpperCase().charAt(0))
                        || !checkUserInput(0, numCols, c[1].charAt(0) - '0'))
                    {
                        System.out.print("\nInsert a row [A - " + (char)(numRows + 'A') + "]" +
                                "and a column [0 - " + numCols +"]!\n");
                        continue;
                    }
                    row = (c[0].toUpperCase().charAt(0)) - 'A';
                    column = c[1].charAt(0) - '0';
                    coords.add(new Coordinates(row, column));
                    break;
                }
            } else {
                break;
            }
        }
        return coords;
    }

    public void show() {
        boolean connected;

        Scanner scanner = new Scanner(System.in);

        showTitle();
        connected = chooseConnection();
        if(!connected)
            System.out.println("Error during connection!");
        System.out.println("Connected!");
        user = readUsername();
        System.out.println("Your username is: " + user);

        Game game = new Game(4);// Instanciated just for try
        game.addPlayer("a", (message -> System.out.println("ciao")));
        game.addPlayer("b", (message -> System.out.println("ciao")));
        game.addPlayer("c", (message -> System.out.println("ciao")));
        game.addPlayer("d", (message -> System.out.println("ciao")));
        game.init();
        try {
            game.refillGameBoard();
        }catch (Exception e){
            e.printStackTrace();
        }

        PlayerView me = new PlayerView(game.getPlayer(0));
        Coordinates[] pvtGoals = new Coordinates[6]; // num of Coordinates to be read from config
        pvtGoals = me.getPrivateGoal().getCoordinates();

        Shelf shelf = new Shelf();
        Shelf privateGoals = new Shelf();

        ShelfView shelfView = new ShelfView(shelf);

        Coordinates[] chosenTiles;
        List<Coordinates> coords;

        while(true){
            GameView modelView = new GameView(game);
            GameBoardView gameBoard = modelView.getGameBoard();

            showBoard(gameBoard);
            showShelf(shelfView);
            showPrivateGoals(modelView.getPlayers()[0].getPrivateGoal().getCoordinates());

            int numRows = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).max().getAsInt()
                    - gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).min().getAsInt() - 1;

            int numCols = gameBoard.getCoords().stream().mapToInt(y -> y.getCOL()).max().getAsInt()
                    - gameBoard.getCoords().stream().mapToInt(y -> y.getCOL()).min().getAsInt() - 1;

            coords = pickTiles(numRows, numCols);
            chosenTiles = new Coordinates[coords.size()];
            for(int i = 0; i < coords.size(); i++){
                chosenTiles[i] = coords.get(i);
            }

            Tile t;
            int column = 0;
            while(true){
                System.out.print("In which column do you want to insert the Tiles? ");
                String input = scanner.nextLine();
                if(!checkUserInput(0, 4, input.charAt(0) - '0')) {
                    System.out.println("\nInsert a valid column!");
                    continue;
                }
                try{
                    column = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                break;
            }

            // Once I have the Tiles to pick
            for(Coordinates el: chosenTiles){
                t = game.getGameBoard().getTile(el);
                game.getGameBoard().setTile(el, null);
                try{
                    shelf.addTile(t, column);
                } catch (IllegalColumnInsertionException e) {
                    e.printStackTrace();
                } catch (NoTileException e) {
                    e.printStackTrace();
                }
            }
            shelfView = new ShelfView(shelf);
        }
    }
}