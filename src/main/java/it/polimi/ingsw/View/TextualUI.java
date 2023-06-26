package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidIPAddress;
import it.polimi.ingsw.Exceptions.InvalidPort;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Utilities.ConsoleColors;
import it.polimi.ingsw.Model.Utilities.IPAddressValidator;
import it.polimi.ingsw.ModelView.*;
import it.polimi.ingsw.Network.ClientImplementation;


import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;


public class TextualUI extends UserInterface {

    private final Scanner in;
    private final PrintStream out;
    private String username;

    private int[] freeSpacesInMyShelf;
    private int maxFreeSpacesInMyShelf;
    private GameBoardView gameBoardView;
    private PlayerView[] players;
    private String cheater;
    private int numOfActivePlayers;
    private int currentPlayer;
    private GlobalGoalView[] globalGoals;

    private boolean gameStarted = false;
    private boolean gameEnded = false;
    private GameView gameView;

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

    //GameBoard gameBoard;

    private boolean isUIClosable(){
        synchronized (lockMainThread){
            return this.gameEnded;
        }
    }

    private final Object lockLogin = new Object();
    private final Object lockQueue = new Object();
    private final Object lockMainThread = new Object();

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
        /*
        this.gameView =
        this.players = gameView.

        this.maxFreeSpacesInMyShelf = 0;
        this.freeSpacesInMyShelf = new int[Shelf.getColumns()];
        for(int i = 0; i < freeSpacesInMyShelf.length; i++){
            freeSpacesInMyShelf[i] = 0;
        }*/

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

        String ip;
        String port;
        do{
            out.println("Please provide the IP address of the server:");
            ip = in.nextLine();
            out.println("Please provide the port of the " + (choice == 1 ? "RMI" : "Socket") + " server:");
            port = in.nextLine();
        }while(!IPAddressValidator.isValidIPAddress(ip) || !IPAddressValidator.isValidPort(port));

        if(choice == 1){

            out.println("Connecting with RMI...");
            try{
                this.setUpRMIClient(ip, port);
            }catch (RemoteException | NotBoundException | InvalidIPAddress | InvalidPort ex ){
                out.println("Cannot connect with RMI. Make sure the IP and Port provided are valid and try again later...");
                return false;
            }
        }else{

            out.println("Connecting with socket...");
            try{
                this.setUpSocketClient(ip, port);
            }catch (RemoteException | NotBoundException | InvalidIPAddress | InvalidPort ex ){
                out.println("Cannot connect with socket. Make sure the IP and Port provided are valid and try again later...");
                out.println("Cannot connect with socket. Make sure the IP and Port provided are valid and try again later...");
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
                in.nextLine();
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
            this.username = in.nextLine();
            if(this.username.contains(" ")) out.println("Spaces are not allowed in the username");
        }while(this.username.contains(" "));

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
        int maxRow = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).max().getAsInt() + 1;
        int minRow = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).min().getAsInt();

        int maxCol = gameBoard.getCoords().stream().mapToInt(y -> y.getCOL()).max().getAsInt() + 1;
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

    /*
    public void showMyShelf(ShelfView shelf){
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
    */

    public void showShelves(PlayerView[] players){
        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        TileView tile;
        out.println("\n\n\n");

        // display names and scores
        out.print("\t");
        for(int i = 0; i < players.length; i++){
            out.print(players[i].getUsername() + "'s Shelf [" + players[i].getScore() + "]\t\t\t\t");
        }
        out.println("\n");

        for(int i = 0; i < r; i++){
            for(PlayerView p: players){
                for(int k = 0; k < c; k++){
                    tile = p.getShelf().getTile(new Coordinates(i, k));
                    printTile(tile);
                    //out.print(ConsoleColors.RESET.getCode() + "");
                }
                out.print("\t\t");  // double tab from a player's shelf to another
            }
            out.print(ConsoleColors.RESET.getCode() + "");
            out.print("\n\n");
        }
    }

    private void showCheater(String cheater){
        out.println(cheater + " tried to cheat!");
    }

    private void showGlobalGoals(GlobalGoalView[] globalGoals){
        out.println("Global goals: ");
        for(int i = 0; i < globalGoals.length; i++){
            out.println((i + 1) + globalGoals[i].getDescription() + "[" + globalGoals[i].getCurrentScore() + "]");
        }
    }

    private List<Coordinates> pickTiles(GameBoardView gameBoard, int numRows, int numCols, int rowOffset, int colOffset) {
        List<Coordinates> coords = new ArrayList<>();

        int chosenRow, chosenColumn, choice;
        String input;
        Coordinates singleTileCoord;

        int maxPickableTiles = 3;

        while(true) {
            choice = readChoiceFromInput("Pick a tile", "Done");

            if (choice == 1) {
                // Pick a tile
                while(true){
                    //checks that one more tile can be picked
                    if(coords.size() == maxPickableTiles ){
                        out.println("Max number of tiles [" + maxPickableTiles + "] selected" );
                        break;
                    }
                    //checks that one more tile can fit in the shelf
                    if(maxFreeSpacesInMyShelf == coords.size()){
                        out.println("In your shelf there is space for at most " + maxFreeSpacesInMyShelf + " tiles");
                        break;
                    }

                    System.out.print("Insert the coordinates [ROW] [COLUMN]: ");
                    input = in.nextLine();
                    String[] readCoords;
                    readCoords = input.split("\\s+");    // split with one or multiple spaces
                    if( (readCoords.length > 2) || (readCoords.length < 2)
                            || readCoords[0].length() > 1 || readCoords[0].length() <= 0
                            || readCoords[1].length() > 1 || readCoords[1].length() <= 0
                            || !checkUserInput('A', numRows + 'A', readCoords[0].toUpperCase().charAt(0))
                            || !checkUserInput(0, numCols, readCoords[1].charAt(0) - '0'))
                    {
                        System.out.print("\nInsert a row [A - " + (char)(numRows + 'A') + "]" +
                                "and a column [0 - " + numCols +"]!\n");
                        continue;
                    }

                    chosenRow = (readCoords[0].toUpperCase().charAt(0)) - 'A' + rowOffset;
                    chosenColumn = readCoords[1].charAt(0) - '0' + colOffset;
                    singleTileCoord = new Coordinates(chosenRow, chosenColumn);

                    if(!gameBoard.getCoords().contains(singleTileCoord)){
                        System.out.println("Insert valid coordinates!");
                        continue;
                    }
                    // if it isn't already picked
                    if(gameBoard.getTile(singleTileCoord) != null){
                        //checks that this tile has not been already picked and that is next to one of the previously picked ones
                        boolean nextTo = false;
                        int i;
                        for(i = 0; i < coords.size(); i++){
                            if(coords.get(i).getROW() == chosenRow && coords.get(i).getCOL() == chosenColumn){
                                out.println("You have already selected this tile");
                                break;
                            }
                            if(( ( coords.get(i).getCOL() + 1 == chosenColumn || coords.get(i).getCOL() - 1 == chosenColumn ) && coords.get(i).getROW() == chosenRow )
                                    || ( ( coords.get(i).getROW() + 1 == chosenRow || coords.get(i).getROW() - 1 == chosenRow ) && coords.get(i).getCOL() == chosenColumn ))
                                nextTo = true;
                        }
                        if(!nextTo && coords.size() > 0){
                            out.println("This tile is not next to one of the others you selected");
                            break;
                        }
                        //checks that this tile is on the same column or on the same row with the previously picked ones
                        boolean sameRow = true;
                        boolean sameColumn = true;
                        for(i = 0; i < coords.size() && (sameRow || sameColumn); i++) {
                            if(coords.get(i).getROW() != chosenRow) sameRow = false;
                            if(coords.get(i).getCOL() != chosenColumn) sameColumn = false;
                        }
                        //coords size può essere 0
                        if( !(sameRow && sameColumn) ){
                            // qui c'è un errore nell'if
                            if( coords.size() > 0 && (!sameRow && !sameColumn)){
                                out.println("The selected tiles must be on the same line");
                                break;
                            }
                        }
                        // all the checks are done, it can be added to the list of coords
                        coords.add(new Coordinates(chosenRow, chosenColumn));
                        break;
                    }else{
                        out.println("This tile was already picked! Pick another one");
                        // continue;
                    }
                }
            } else {
                break;
            }
        }
        return coords;
    }

    private Message doTurnAction(GameBoardView gameBoard, PlayerView playerView){
        // to display the gameBoard and pick the tiles correctly
        int maxRow = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).max().getAsInt();
        int minRow = gameBoard.getCoords().stream().mapToInt(x -> x.getROW()).min().getAsInt();

        int maxCol = gameBoard.getCoords().stream().mapToInt(y -> y.getCOL()).max().getAsInt();
        int minCol = gameBoard.getCoords().stream().mapToInt(y -> y.getCOL()).min().getAsInt();

        Coordinates[] chosenTiles;
        List<Coordinates> coords;

        coords = pickTiles(gameBoard,maxRow - minRow, maxCol - minCol, minRow, minCol);
        int numOfChosenTiles = coords.size();
        chosenTiles = new Coordinates[numOfChosenTiles];
        for(int i = 0; i < coords.size(); i++){
            chosenTiles[i] = coords.get(i);
        }

        TileView t;
        int column = 0;

        while(true){
            out.print("In which column do you want to insert the Tiles? ");
            String input = in.nextLine();
            if(!checkUserInput(0, Shelf.getColumns(), input.charAt(0) - '0')) {
                out.println("\nInsert a valid column!");
                continue;
            }
            try{
                column = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                out.println("Insert a valid input");
                continue;
            }
            if(this.freeSpacesInMyShelf[column] < numOfChosenTiles){
                out.println("The tiles chosen cannot fit in the " + column + " column!");
                continue;
            }
            break;
        }

        // Once I have the Tiles to pick
        // print the Tiles and ask for the order
        out.println("Select the order in which you want to insert the tiles in the " + column + " column: ");
        for(int i = 0; i < chosenTiles.length; i++){
            t = gameBoard.getTile(chosenTiles[i]);
            out.print((i + 1) + ":");
            printTile(t);
            out.println("\n");
        }

        int[] order = new int[chosenTiles.length];
        for(int i = 0; i < order.length; i++){
            order[i] = readNumberFromInput(1, chosenTiles.length);
        }

        // once I have the order, set the Coordinates array to send
        Coordinates[] orderedChosenTiles = new Coordinates[chosenTiles.length];
        for(int i = 0; i < chosenTiles.length; i++){
            orderedChosenTiles[i] = chosenTiles[order[i] - 1];
        }

        return new TurnActionMessage(playerView.getUsername(), orderedChosenTiles, column);
    }

    private void askPlayerNumOfPlayerForLobby(){
        out.println("Insert the number of players for the game:");
        int numOfPlayers = readNumberFromInput(2,4);

        doConnect(this.username, numOfPlayers);
    }

    @Override
    public void update(Message m){
        ClientImplementation.logger.log(Level.INFO,"CLI Received " + m.toString());

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
                ClientImplementation.logger.log(Level.SEVERE, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        ClientImplementation.logger.log(Level.INFO,"CLI: received Reconnect response from server");

    }


    private boolean doLogin() {
        boolean connected;

        connected = chooseConnection();
        if(!connected){
            return false;
        }
        ClientImplementation.logger.log(Level.INFO,"Connected to server!");

        readUsername();
        out.println("You chose the username: " + this.username);
        ClientImplementation.logger.log(Level.INFO,"L'utente ha scelto l'username: " + this.username);

        boolean validUsername = false;

        doReconnect(this.username);
        waitForLoginResponse();

        while(true)
        {
            Message m = this.getFirstMessageFromQueue();

            if(m instanceof UsernameNotFoundMessage){
                ClientImplementation.logger.log(Level.INFO,"L'username scelto è di un nuovo utente");
                chooseLobby();
                waitForLoginResponse();
            }
            else if(m instanceof TakenUsernameMessage){
                ClientImplementation.logger.log(Level.INFO,"L'username scelto è già in uso");
                readUsername();
                out.println("Hai scelto l'username: " + this.username);
                doReconnect(this.username);
                waitForLoginResponse();
                ClientImplementation.logger.log(Level.INFO,"L'utente ha scelto l'username: " + this.username);
            }
            else if(m instanceof NoLobbyAvailableMessage){
                ClientImplementation.logger.log(Level.INFO,"Nessuna lobby esistente");
                out.println("Nessuna lobby esistente. Ripeti la selezione");
                chooseLobby();
                waitForLoginResponse();
            }
            else if(m instanceof LobbyMessage){
                ClientImplementation.logger.log(Level.INFO,"Messaggio giocatori in lobby");
                printPlayersInLobby((LobbyMessage) m);
                break;
            }
            else{
                addMessageToQueue(m);
                waitForLoginResponse();
            }
        }

        return true;
    }

    @Override
    public void run() {
        showTitle();
        if(!doLogin()){
            out.println("An error happened while loggin in, retry later!");
            return;
        }
        out.println("Login effettuato con successo!");
        out.println("In attesa dell'inizio della partita");

        new Thread(){
            @Override
            public void run() {
                super.run();
                while(true){
                    if(isMessagesQueueEmpty()){
                        continue;
                    }

                    Message m = getFirstMessageFromQueue();
                    ClientImplementation.logger.log(Level.INFO, "Inizio gestione messaggio: " + m.getClass());

                    if(m instanceof  LobbyMessage){
                        printPlayersInLobby((LobbyMessage) m);
                    } else if (m instanceof UpdateViewMessage) {
                        //printTheWholeFGame((UpdateViewMessage) m);
                        synchronized (lockMainThread){
                            update(((UpdateViewMessage) m).getGameView());
                            printTheWholeFGame();
                            lockMainThread.notifyAll();
                        }

                    }
                    ClientImplementation.logger.log(Level.INFO, "Fine gestione messaggio: " + m.getClass());
                }
            }
        }.start();

        try{
            synchronized (lockMainThread){
                lockMainThread.wait();
            }
        }catch (InterruptedException e){
            System.err.println("Interrupted while waiting for server: " + e.getMessage());
            ClientImplementation.logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }

        while(!gameEnded){

            //clearConsole();
            PlayerView currPlayer;
            //GameBoardView gameBoardView;

            out.println("Please choose your action:");
            out.println("/help for help");
            String sChoice = in.nextLine();
            switch (sChoice){
                case "/help": {
                    printHelp();
                    break;
                }
                case "/play": {
                    synchronized (lockMainThread){
                        currPlayer = this.players[this.currentPlayer];
                        //gameBoardView = this.gameView.getGameBoard();
                    }
                    if(currPlayer.getUsername().equals(username)){
                        Message turnActionMessage = doTurnAction(this.gameBoardView, currPlayer);
                        notifyListeners(turnActionMessage);
                        ClientImplementation.logger.log(Level.INFO, "Sent TurnActionMessage to listeners");
                    }
                    else{
                        out.println("Not your turn!");
                    }
                    break;
                }
            }
        }
    }

    private GameView update(GameView gv){
        if(gv.getGameBoard() != null) this.gameBoardView = gv.getGameBoard();
        if(this.players == null)
            this.players = new PlayerView[gv.getPlayers().length];
        if(gv.getPlayers() != null){
            for(int i = 0; i < gv.getPlayers().length; i++){
                if(gv.getPlayers()[i] != null) {
                    this.players[i] = gv.getPlayers()[i];
                }
            }
        }
        //once i got the players, update the maxFreeSpaces in my shelf
        int myIndex = -1;
        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i].getUsername().equals(username))
                // IT HAS TO GET HERE, IT'S IMPOSSIBLE TO SKIP -> invece un player è null :)
            {
                myIndex = i;
            }
        }
        this.freeSpacesInMyShelf = new int[Shelf.getColumns()];
        for(int i = 0; i < this.freeSpacesInMyShelf.length; i++)
            this.freeSpacesInMyShelf[i] = 0;

        for(int i = 0; i< freeSpacesInMyShelf.length; i++) freeSpacesInMyShelf[i] = 0;
        for (int i = 0; i < Shelf.getRows(); i++) {
            for (int j = 0; j < Shelf.getColumns(); j++) {
                if(this.players[myIndex].getShelf().getTile(new Coordinates(i,j)) == null) {
                    freeSpacesInMyShelf[j]++;
                }
            }
        }
        this.maxFreeSpacesInMyShelf = 0;
        for(int i = 0; i < freeSpacesInMyShelf.length; i++)
            if(freeSpacesInMyShelf[i] > maxFreeSpacesInMyShelf)
                maxFreeSpacesInMyShelf = freeSpacesInMyShelf[i];

        this.cheater = gv.getCheater(); // can be null if no-one cheated
        if(gv.getNumOfActivePlayers() != null) this.numOfActivePlayers = gv.getNumOfActivePlayers();
        if(gv.getGlobalGoals() != null) this.globalGoals = gv.getGlobalGoals();
        if(gv.getCurrentPlayer() != null) this.currentPlayer = gv.getCurrentPlayer();
        return gv;
    }

    private void printHelp(){
        out.println("Commands:");
        out.println("/play to play your turn");
        out.println("/help to show this message");
    }

    private void printTheWholeFGame(){
        // start the game
        int myIndex = 0;
        // get my player index
        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i].getUsername().equals(username))
                myIndex = i;
        }


        PrivateGoalView myPrivateGoalView = this.players[myIndex].getPrivateGoal();
        int playerIndex = this.currentPlayer;
        //numOfActivePlayers;

        if(this.cheater != null)
            showCheater(this.cheater);
        showShelves(this.players);
        showBoard(this.gameBoardView);
        showGlobalGoals(this.globalGoals);
        showPrivateGoals(myPrivateGoalView.getCoordinates());

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
                doConnect(this.username, 1);
                break;
        }
    }

    public final static void clearConsole()
    {
        System.out.print("\033[H\033[2J");
        System.out.flush();
        /*
        try
        {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }

        }
        catch (final Exception e)
        {
            e.printStackTrace();
        }*/
    }

}