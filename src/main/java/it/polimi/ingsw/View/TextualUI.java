package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.AppClientImplementation;
import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.NoTileException;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Utilities.ConsoleColors;
import it.polimi.ingsw.ModelView.*;


import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;


public class TextualUI extends ClientManager {

    private Scanner in;
    private PrintStream out;
    private String userName;

    private Queue<Message> recievedMessages = new LinkedList<>();

    // Write this title in a config file
    String r1 = " __    __           ______ _           _  __  _";
    String r2 = "|  \\  /  |         /  ____| |         | |/ _|(_)";
    String r3 = "| \\ \\/ / |_   _   |  (___ | |__   ___ | | |_  _  ___";
    String r4 = "| |\\__/| | | | |   \\___  \\|  _ \\ / _ \\| |  _|| |/ _ \\";
    String r5 = "| |    | | |_| |    ___)  | | | |  __/| | |  | |  __/";
    String r6 = "|_|    |_|\\__, |   |_____/|_| |_|\\___/|_|_|  |_|\\___/";
    String r7 = "            _/ |";
    String r8 = "           |__/";

    GameBoard gameBoard;

    private final Object lockLogin = new Object();
    private final Object lockQueue = new Object();

    private boolean isMessagesQueueEmpty(){
        synchronized (lockQueue){
            return this.recievedMessages.isEmpty();
        }
    }

    private Message getFirstMessageFromQueue(){
        synchronized (lockQueue){
            return this.recievedMessages.poll();
        }
    }

    private void addMessageToQueue(Message m){
        synchronized (lockQueue){
            this.recievedMessages.add(m);
        }
    }


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

                if (choice >= 1 && choice <= 2) {
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
            try{
                this.setUpSocketClient();
            }catch (RemoteException | NotBoundException ex ){
                out.println("Cannot connect with RMI. Trying with socket...");
                return false;
            }
        }
        return true;
    }

    private boolean checkUserInput(int lowerBound, int upperBound, int input){
        return input >= lowerBound && input <= upperBound;
    }

    private int readChoiceFromInput(String option_1, String option_2){
        String input;
        int choice = -1;

        do{
            out.println("1 - " + option_1);
            out.println("2 - " + option_2 + "\n");
            try{
                choice = in.nextInt();
            }catch ( InputMismatchException ex){
                choice = -1;
            }
        }while (choice<1 || choice>2);

        return choice;
    }

    private int readNumberFromInput(Integer lowerBound, Integer upperBound){
        String input;
        boolean valid = false;
        int number = -1;

        do{
            input = in.nextLine();

            try {
                number = Integer.parseInt(input);
                valid = number >= lowerBound && number <= upperBound;
            }catch (NumberFormatException ex){
                valid = false;
            }

        }while (!valid);

        return number;
    }

    private void readUsername(){
        do{
            out.println("Insert username:");
            this.userName = in.nextLine();
            if(this.userName.contains(" ")) out.println("Spaces are not allowed in the username");
        }while(this.userName.contains(" "));

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

    public void showBoard(GameBoardView gameBoard){
        // print the board -> using a 4 player board for a first try
        int maxRow = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).max().getAsInt();
        int minRow = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).min().getAsInt();

        int maxCol = gameBoard.getCoords().stream().mapToInt(y -> y.getCOL()).max().getAsInt();
        int minCol = gameBoard.getCoords().stream().mapToInt(y -> y.getCOL()).min().getAsInt();

        String color;

        Set<Coordinates> coords = gameBoard.getCoords();

        out.println("\n\n\n\n\t\t\t\tGame Board");
        out.print("     ");

        for(int c = minCol; c < maxCol; c++){
            out.print(" " + (c - minCol) + "   ");
        }
        out.print("\n\n");

        for(int r = minRow; r < maxRow; r++){
            out.print(" " + (char)(r + 65 - minRow) + "   ");
            for(int c = minCol; c < maxCol; c++){
                if(coords.contains(new Coordinates(r, c)) && gameBoard.getTile(new Coordinates(r, c)) != null){
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

    private void printTile(TileView tileView){
        String color;
        if(tileView != null){
            color = getColorCode(tileView);
        }else{
            color = ConsoleColors.BROWN_BACKGROUND.getCode();
        }
        out.print(color + "   " + ConsoleColors.RESET.getCode() + "  ");
    }

    public void showPrivateGoals(Coordinates[] privateGoals){
        out.println("\n\n\tMy private goals\n\n");
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
            out.print(ConsoleColors.RESET.getCode() + "");
            out.print("\n\n");
        }
    }

    public void showShelf(ShelfView shelf){
        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        TileView tile;

        out.println("\n\n\n\n\t\tMy Shelf\n\n");
        for(int i = 0; i < c; i++)
            out.print(" " + i + "   ");
        out.print("\n\n");

        for(int i = 0; i < r; i++){
            for(int k = 0; k < c; k++){
                tile = shelf.getTile(new Coordinates(i, k));
                printTile(tile);
            }
            out.print(ConsoleColors.RESET.getCode() + "");
            out.print("\n\n");
        }
    }

    private List<Coordinates> pickTiles(int numRows, int numCols){
        /* Todo: check that the chosen Tiles from the board are actually pickable, on the same line, maximum 3
         *   -> controller*/
        List<Coordinates> coords = new ArrayList<>();

        int row, column, choice;
        String input;

        while(true) {
            choice = readChoiceFromInput("Pick a tile", "Done");
            if (choice == 1) {
                // Pick a tile
                while(true){
                    System.out.print("Insert the coordinates [ROW] [COLUMN]: ");
                    input = in.nextLine();
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

    public void showProva() {
        showTitle();
        //boolean connected;
        //connected = chooseConnection();
        //if(!connected)
        //    return;
        out.println("Connected!");
        readUsername();

        out.println("Your username is: " + this.userName);

        Game game = new Game(4);// Instanciated just for try
        game.addPlayer("a", (message -> out.println("ciao")));
        game.addPlayer("b", (message -> out.println("ciao")));
        game.addPlayer("c", (message -> out.println("ciao")));
        game.addPlayer("d", (message -> out.println("ciao")));
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
                out.print("In which column do you want to insert the Tiles? ");
                String input = in.nextLine();
                if(!checkUserInput(0, 4, input.charAt(0) - '0')) {
                    out.println("\nInsert a valid column!");
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

    private void askPlayerNumOfPlayerForLobby(){
        out.println("Insert the number of players for the game:");
        int numOfPlayers = readNumberFromInput(2,4);

        doConnect(this.userName, numOfPlayers);
    }

    @Override
    public void update(Message m){
        AppClientImplementation.logger.log(Level.INFO,"CLI Received " + m.toString());

        addMessageToQueue(m);
        synchronized (lockLogin) {
            lockLogin.notifyAll();
        }
    }

    private void waitForLoginResponse(){
        synchronized (lockLogin){
            try{
                lockLogin.wait();
            }catch (InterruptedException e){
                System.err.println("Interrupted while waiting for server: " + e.getMessage());
                AppClientImplementation.logger.log(Level.SEVERE, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        AppClientImplementation.logger.log(Level.INFO,"CLI: received Reconnect response from server");

    }


    private void doLogin() {
        boolean connected;

        connected = chooseConnection();
        if(!connected){
            return;
        }
        AppClientImplementation.logger.log(Level.INFO,"Connected to server!");

        readUsername();
        out.println("Hai scelto l'username: " + this.userName);
        AppClientImplementation.logger.log(Level.INFO,"L'utente ha scelto l'username: " + this.userName);

        boolean validUsername = false;

        doReconnect(this.userName);
        waitForLoginResponse();

        while(true)
        {
            Message m = this.getFirstMessageFromQueue();

            if(m instanceof NoUsernameToReconnectMessage){
                AppClientImplementation.logger.log(Level.INFO,"L'username scelto è di un nuovo utente");
                chooseLobby();
                waitForLoginResponse();
            }
            else if(m instanceof TakenUsernameMessage){
                AppClientImplementation.logger.log(Level.INFO,"L'username scelto è già in uso");
                readUsername();
                out.println("Hai scelto l'username: " + this.userName);
                doReconnect(this.userName);
                waitForLoginResponse();
                AppClientImplementation.logger.log(Level.INFO,"L'utente ha scelto l'username: " + this.userName);
            }
            else if(m instanceof NoLobbyAvailableMessage){
                AppClientImplementation.logger.log(Level.INFO,"Nessuna lobby esistente");
                out.println("Nessuna lobby esistente. Ripeti la selezione");
                chooseLobby();
                waitForLoginResponse();
            }
            else if(m instanceof GameServerMessage){
                addMessageToQueue(m);
                waitForLoginResponse();
            }
            else if(m instanceof LobbyMessage){
                AppClientImplementation.logger.log(Level.INFO,"Messaggio giocatori in lobby");
                printPlayersInLobby((LobbyMessage) m);
                break;
            }
            else{
                AppClientImplementation.logger.log(Level.INFO,"Invalid message recieved; ignoring it.");
                AppClientImplementation.logger.log(Level.INFO,m.toString());
                throw new RuntimeException("Invalid message recieved;");
            }
        }

    }

    private void connectToGameServer(GameServerMessage m){

        cleanListeners();

        addListener((message) -> {
            try {
                m.getServer().handleMessage(message, client);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void run() {
        showTitle();
        doLogin();
        out.println("Login effettuato con successo!");
        out.println("In attesa dell'inizio della partita");
        while(true){
            if(isMessagesQueueEmpty()){
                continue;
            }

            Message m = this.getFirstMessageFromQueue();
            if(m instanceof  LobbyMessage){
                printPlayersInLobby((LobbyMessage) m);
            } else if (m instanceof UpdateViewMessage) {
                //inizia la partita
            }else if(m instanceof GameServerMessage){
                AppClientImplementation.logger.log(Level.INFO,"Switchato listener a GameServer");
                connectToGameServer((GameServerMessage) m);
            }
            System.out.println(m.toString());

        }

    }

    private void printPlayersInLobby(LobbyMessage m){
        out.println("Playes in lobby:");
        m.getPlayers().stream().forEach(x -> out.println(x));
    }

    private void chooseLobby(){
        out.println("Choose a lobby:");
        out.println("1. Create a new lobby");
        out.println("2. Join an existing lobby");

        int choice = readNumberFromInput(1,2);

        switch (choice){
            case 1:
                askPlayerNumOfPlayerForLobby();
                break;
            case 2:
                doConnect(this.userName, 1);
                break;
        }
    }


}