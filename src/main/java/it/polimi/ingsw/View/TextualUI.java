package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidIPAddress;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Utilities.Config;
import it.polimi.ingsw.Utilities.ConsoleColors;
import it.polimi.ingsw.Utilities.IPAddressValidator;
import it.polimi.ingsw.ModelView.*;
import it.polimi.ingsw.Network.ClientImplementation;


import java.io.PrintStream;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

/**
 * Class representing the TUI, thus a {@link UserInterface} implementing the {@link View}.
 */
@SuppressWarnings("ALL")
public class TextualUI extends UserInterface {

    private final Scanner in;
    private final PrintStream out;
    private String username;

    private int[] freeSpacesInMyShelf;
    private int maxFreeSpacesInMyShelf;
    private GameBoardView gameBoardView;
    private PlayerView[] players;
    private int numOfActivePlayers;
    private int currentPlayer;
    private GlobalGoalView[] globalGoals;
    private boolean gameEnded;
    private Queue<Message> receivedMessages;
    private final Object lockLogin;
    private final Object lockQueue;
    private final Object lockMainThread;

    /**
     * Constructor of a {@code TextualUI}.
     */
    public TextualUI() {
        super();
        in = new Scanner(System.in);
        out = new PrintStream(System.out, true);
        lockLogin = new Object();
        lockQueue = new Object();
        lockMainThread = new Object();
        init();
    }

    @Override
    public void update(Message m){
        ClientImplementation.logger.log(Level.INFO,"CLI Received " + m.toString());

        addMessageToQueue(m);
        synchronized (lockLogin) {
            lockLogin.notifyAll();
        }
    }

    private void init(){
        username = null;
        freeSpacesInMyShelf = new int[Shelf.getColumns()];
        maxFreeSpacesInMyShelf = 0;
        gameBoardView = null;
        players = null;
        numOfActivePlayers = 0;
        currentPlayer = -1;
        globalGoals = null;
        gameEnded = false;

        receivedMessages = new LinkedList<>();
    }

    private boolean isMessagesQueueEmpty(){
        synchronized (lockQueue){
            return this.receivedMessages.isEmpty();
        }
    }

    private Message popMessageFromQueue(){
        synchronized (lockQueue){
            return this.receivedMessages.poll();
        }
    }

    private void addMessageToQueue(Message m){
        synchronized (lockQueue){
            this.receivedMessages.add(m);
        }
    }

    private void showTitle(){
        for(String row : Config.getInstance().getCliTitle()){
            out.println(row);
        }
    }

    private String getColorCode(TileView tile){
        TileColor tc = tile.getColor();
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

    private int readChoiceFromInput(String option_1, String option_2){
        String inputRead;
        int choice = -1;

        while(true){
            out.println("1 - " + option_1);
            out.println("2 - " + option_2);
            try{
                inputRead = in.nextLine();
            }catch ( InputMismatchException ex){
                out.println("Invalid selection!");
                continue;
            }

            try{
                choice = Integer.parseInt(inputRead);
            } catch (NumberFormatException e) {
                out.println("Invalid selection!");
                continue;
            }
            if(choice != 1 && choice != 2) out.println("Invalid selection!");
            else break;
        }
        return choice;
    }

    private int readNumberFromInput(Integer lowerBound, Integer upperBound){
        String input;
        boolean valid;
        int number = -1;

        do{
            input = in.nextLine();
            try {
                number = Integer.parseInt(input);
                valid = (number >= lowerBound && number <= upperBound);
            }catch (NumberFormatException ex){
                valid = false;
            }
            if(!valid) out.println("Input is not valid");

        }while (!valid);

        return number;
    }

    private boolean checkUserInput(int lowerBound, int upperBound, int input){
        return input >= lowerBound && input <= upperBound;
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

    private boolean chooseConnection(){
        out.println("Choose the connection type:");
        int choice = readChoiceFromInput("RMI","SOCKET");

        String ip;
        do {
            out.println("Please provide the IP address or the URL of the server:");
            ip = in.nextLine();
        }while(!IPAddressValidator.isValidIPAddress(ip) && !IPAddressValidator.isValidURL(ip));

        if(choice == 1){

            out.println("Connecting with RMI...");
            try{
                this.setUpRMIClient(ip);
            }catch (RemoteException | NotBoundException | InvalidIPAddress ex ){
                out.println("Cannot connect with RMI. Make sure the IP provided is valid and try again later...");
                return false;
            }
        }else{

            out.println("Connecting with socket...");
            try{
                this.setUpSocketClient(ip);
            }catch (RemoteException | InvalidIPAddress ex ){
                out.println("Cannot connect with socket. Make sure the IP provided is valid and try again later...");
                return false;
            }
        }
        return true;
    }

    private void readUsername(){
        do{
            out.println("Insert username:");
            this.username = in.nextLine();
            if(this.username.contains(" ")) out.println("Spaces are not allowed in the username");
        }while(this.username.contains(" ") || this.username.equals(""));

        ClientImplementation.logger.log(Level.INFO,"The user chose the username: " + this.username);
        doReconnect(this.username);
        waitForLoginResponse();
    }

    private void chooseLobby(){
        out.println("Choose to create a lobby or to join a lobby:");
        int choice = readChoiceFromInput("Create a new lobby","Join an existing lobby");

        switch (choice){
            case 1:
                askNumOfPlayers();
                break;
            case 2:
                doConnect(this.username, 1);
                break;
        }
        waitForLoginResponse();
    }

    private void askNumOfPlayers(){
        out.println("Insert the number of players for the game:");
        int numOfPlayers = readNumberFromInput(2,Config.getInstance().getMaxNumberOfPlayers());
        doConnect(this.username, numOfPlayers);
    }

    private void printPlayersInLobby(LobbyMessage m){
        out.println("Playes in lobby:");
        m.getPlayers().stream().forEach(x -> out.println(x));
    }

    private void doLogin() {

        readUsername();

        while(true)
        {
            Message m = this.popMessageFromQueue();

            if(m instanceof UsernameNotFoundMessage){
                ClientImplementation.logger.log(Level.INFO,"The chosen username is not taken");
                chooseLobby();
            }
            else if(m instanceof TakenUsernameMessage){
                ClientImplementation.logger.log(Level.INFO,"The chosen username is already taken");
                out.println("The chosen username is already taken");
                readUsername();
            }
            else if(m instanceof NoLobbyAvailableMessage){
                ClientImplementation.logger.log(Level.INFO,"There are no lobbies available at the moment");
                out.println("There are no lobbies available at the moment, create a new one");
                chooseLobby();
            }
            else if(m instanceof LobbyMessage){
                ClientImplementation.logger.log(Level.INFO,"Players in lobby message");
                printPlayersInLobby((LobbyMessage) m);
                break;
            }
            else if(m instanceof UpdateViewMessage){
                ClientImplementation.logger.log(Level.INFO,"Update view message");
                addMessageToQueue(m);
                break;
            }
            else{
                ClientImplementation.logger.log(Level.INFO,"Waiting for response...");
                addMessageToQueue(m);
                waitForLoginResponse();
            }
        }
    }

    private void update(GameView gv){
        if(gv.getGameBoard() != null) this.gameBoardView = gv.getGameBoard();
        if(gv.getPlayers() != null){
            if(this.players == null) this.players = new PlayerView[gv.getPlayers().length];
            int myIndex = -1;
            for(int i = 0; i < gv.getPlayers().length; i++){
                if(gv.getPlayers()[i] != null) {
                    this.players[i] = gv.getPlayers()[i];
                    if(gv.getPlayers()[i].getUsername().equals(username)) myIndex = i;
                }
            }
            if(myIndex != -1){
                for(int i = 0; i < this.freeSpacesInMyShelf.length; i++)
                    freeSpacesInMyShelf[i] = 0;
                maxFreeSpacesInMyShelf = 0;

                for (int i = 0; i < Shelf.getRows(); i++) {
                    for (int j = 0; j < Shelf.getColumns(); j++) {
                        if(this.players[myIndex].getShelf().getTile(new Coordinates(i,j)) == null) {
                            freeSpacesInMyShelf[j]++;
                            if(freeSpacesInMyShelf[j] > maxFreeSpacesInMyShelf) maxFreeSpacesInMyShelf = freeSpacesInMyShelf[j];
                        }
                    }
                }
            }
        }

        if(gv.getCheater() != null) out.println(gv.getCheater() + " tried to cheat!");
        if(gv.getNumOfActivePlayers() != null) {
            if(numOfActivePlayers != 0){
                if(gv.getNumOfActivePlayers() == 1) out.println("Other players disconnected, wait for them to reconnect or wait to win by forfeit");
                else if(gv.getNumOfActivePlayers() == 2 && numOfActivePlayers == 1) out.println("a player reconnected, the game can go on");
                else if(gv.getNumOfActivePlayers() > numOfActivePlayers) out.println("a player reconnected");
                else if(gv.getNumOfActivePlayers() < numOfActivePlayers) out.println("a player disconnected");
            }

            numOfActivePlayers = gv.getNumOfActivePlayers();
        }
        if(gv.getGlobalGoals() != null) this.globalGoals = gv.getGlobalGoals();
        if(gv.getCurrentPlayer() != null) this.currentPlayer = gv.getCurrentPlayer();
    }

    private void printTheWholeGame(){
        int myIndex = -1;
        int winnerIndex = -1;
        for (int i = 0; i < this.players.length; i++) {
            if (players[i].getUsername().equals(username))
                myIndex = i;
            if(players[i].isWinner())
                winnerIndex = i;
        }

        clearConsole();
        showBoard();
        showGlobalGoals();
        showShelves();
        showPrivateGoal(this.players[myIndex].getPrivateGoal().getCoordinates());
        if(winnerIndex != -1){
            gameEnded = true;
            if(myIndex == winnerIndex) out.println("YOU WON!");
            else out.println("YOU LOST!");
        }
        else if(currentPlayer != -1)
            out.println("It is "+ ( currentPlayer == myIndex ? "your" : ( players[currentPlayer].getUsername() + "'s" ) ) + " turn" );
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

    private void showBoard(){
        // print the board -> using a 4 player board for a first try
        int maxRow = gameBoardView.getCoords().stream().mapToInt(x -> x.getROW()).max().getAsInt() + 1;
        int minRow = gameBoardView.getCoords().stream().mapToInt(x -> x.getROW()).min().getAsInt();

        int maxCol = gameBoardView.getCoords().stream().mapToInt(y -> y.getCOL()).max().getAsInt() + 1;
        int minCol = gameBoardView.getCoords().stream().mapToInt(y -> y.getCOL()).min().getAsInt();

        String color;

        Set<Coordinates> coords = gameBoardView.getCoords();

        out.println("\n\n\n\n\t\t\t\tGame Board");
        out.print("     ");

        for(int c = minCol; c < maxCol; c++){
            out.print(" " + (c - minCol) + "   ");
        }
        out.print("\n\n");

        for(int r = minRow; r < maxRow; r++){
            out.print(" " + (char)(r + 65 - minRow) + "   ");
            for(int c = minCol; c < maxCol; c++){
                if(coords.contains(new Coordinates(r, c)) && gameBoardView.getTile(new Coordinates(r, c)) != null){
                    color = getColorCode(gameBoardView.getTile(new Coordinates(r, c)));
                }else{
                    color = ConsoleColors.RESET.getCode();
                }
                out.print(color + "   " + ConsoleColors.RESET.getCode() + "  ");
            }
            out.print(ConsoleColors.RESET.getCode() + "");
            out.print("\n\n");
        }
    }

    private void showPrivateGoal(Coordinates[] privateGoal){
        out.println("\n\n\tMy private goal\n\n");
        int r = Shelf.getRows();
        int c = Shelf.getColumns();
        TileView tile = null;

        for(int i = 0; i < r; i++){
            for(int k = 0; k < c; k++){
                for(int color = 0; color < privateGoal.length; color++){
                    if(privateGoal[color].equals(new Coordinates(i, k))){
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

    /**
     * Displays the game shelves.
     */
    public void showShelves(){
        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        TileView tile;
        out.println("\n\n\n");

        // display names and scores
        out.print("\t");
        for(int i = 0; i < players.length; i++){
            String currUser = players[i].getUsername();
            out.print( ( currUser.equals(username) ? "My" :
                    ( (currUser.length()>15 ? currUser.substring(0,15)+"..." : currUser ) + "'s") ) + " Shelf [" + players[i].getScore() + "]\t\t\t\t");
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

    private void showGlobalGoals(){
        out.println("Global goals: ");
        for(int i = 0; i < globalGoals.length; i++){
            out.println((i + 1) + ": " + globalGoals[i].getDescription() + "[" + globalGoals[i].getCurrentScore() + "]");
        }
    }

    private List<Coordinates> pickTiles(int numRows, int numCols, int rowOffset, int colOffset) {
        List<Coordinates> coords = new ArrayList<>();

        int chosenRow, chosenColumn, choice;
        String input;
        Coordinates singleTileCoord;

        while(true) {
            if(coords.size() != 0)choice = readChoiceFromInput("Pick another tile", "Order the chosen tiles");
            else{
                out.println("Pick a tile");
                choice = 1;
            }

            if (choice == 1) {
                // Pick a tile

                //checks that one more tile can be picked
                if(coords.size() == Config.getInstance().getMaxNumOfChosenTiles() ){
                    out.println("You can not select more than " + Config.getInstance().getMaxNumOfChosenTiles() + " tiles" );
                    break;
                }
                //checks that one more tile can fit in the shelf
                if(maxFreeSpacesInMyShelf == coords.size()){
                    out.println("In your shelf there is space for at most " + maxFreeSpacesInMyShelf + " tiles");
                    break;
                }

                out.println("Insert the coordinates [ROW] [COLUMN]: ");
                input = in.nextLine();
                String[] readCoords;
                readCoords = input.split("\\s+");    // split with one or multiple spaces
                if( (readCoords.length > 2) || (readCoords.length < 2)
                        || readCoords[0].length() > 1 || readCoords[0].length() <= 0
                        || readCoords[1].length() > 1 || readCoords[1].length() <= 0
                        || !checkUserInput('A', numRows + 'A', readCoords[0].toUpperCase().charAt(0))
                        || !checkUserInput(0, numCols, readCoords[1].charAt(0) - '0'))
                {
                    out.println("Insert a row [A - " + (char)(numRows + 'A') + "]" +
                            " and a column [0 - " + numCols +"]");
                    continue;
                }

                chosenRow = (readCoords[0].toUpperCase().charAt(0)) - 'A' + rowOffset;
                chosenColumn = readCoords[1].charAt(0) - '0' + colOffset;
                singleTileCoord = new Coordinates(chosenRow, chosenColumn);

                //check that the coordinates correspond to a tile on the board
                if(!gameBoardView.getCoords().contains(singleTileCoord) || gameBoardView.getTile(singleTileCoord) == null){
                    out.println("These coordinates are not valid");
                    continue;
                }

                //checks that the chosen tile is pickable
                if(!gameBoardView.isPickable(singleTileCoord)){
                    out.println("You can not pick this tile");
                    continue;
                }

                //checks that this tile has not been already picked and that is next to one of the previously picked ones
                boolean nextTo = false;
                boolean alreadyPicked = false;
                int i;
                for(i = 0; i < coords.size(); i++){
                    if(coords.get(i).getROW() == chosenRow && coords.get(i).getCOL() == chosenColumn){
                        alreadyPicked = true;
                        break;
                    }
                    if(( ( coords.get(i).getCOL() + 1 == chosenColumn || coords.get(i).getCOL() - 1 == chosenColumn ) && coords.get(i).getROW() == chosenRow )
                            || ( ( coords.get(i).getROW() + 1 == chosenRow || coords.get(i).getROW() - 1 == chosenRow ) && coords.get(i).getCOL() == chosenColumn ))
                        nextTo = true;
                }
                if(alreadyPicked){
                    out.println("You have already selected this tile");
                    continue;
                }
                if(!nextTo && coords.size() > 0){
                    out.println("This tile is not next to one of the others you selected");
                    continue;
                }
                //checks that this tile is on the same column or on the same row with the previously picked ones
                boolean sameRow = true;
                boolean sameColumn = true;
                for(i = 0; i < coords.size()-1 && (sameRow || sameColumn); i++) {
                    if(coords.get(i).getROW() != coords.get(i+1).getROW()) sameRow = false;
                    if(coords.get(i).getCOL() != coords.get(i+1).getCOL()) sameColumn = false;
                }
                //coords size can be 0 or 1
                if( !(sameRow && sameColumn) ){
                    // qui c'è un errore nell'if
                    if( (sameRow && coords.get(0).getROW() != chosenRow) || (sameColumn && coords.get(0).getCOL() != chosenColumn)){
                        out.println("The selected tiles must be on the same line");
                        continue;
                    }
                }
                // all the checks are done, it can be added to the list of coords
                coords.add(new Coordinates(chosenRow, chosenColumn));
            } else {
                break;
            }
        }
        return coords;
    }

    private Message doTurnAction(){
        // to display the gameBoard and pick the tiles correctly
        int maxRow = gameBoardView.getCoords().stream().mapToInt(x -> x.getROW()).max().getAsInt();
        int minRow = gameBoardView.getCoords().stream().mapToInt(x -> x.getROW()).min().getAsInt();

        int maxCol = gameBoardView.getCoords().stream().mapToInt(y -> y.getCOL()).max().getAsInt();
        int minCol = gameBoardView.getCoords().stream().mapToInt(y -> y.getCOL()).min().getAsInt();

        while (true) {
            List<Coordinates> coords = pickTiles(maxRow - minRow, maxCol - minCol, minRow, minCol);

            // can select the order for just 1 out of 3, the order of the other two tiles remains the same (relatively)
            // or must select the order <-
            Integer[] order = new Integer[coords.size()];
            while(true){
                // Once I have the Tiles to pick, print those Tiles and ask for their order
                out.println("Insert the numbers corresponding to the tiles in the order according to which you want them:");
                for (int i = 0; i < coords.size(); i++) {
                    TileView t = gameBoardView.getTile(coords.get(i));
                    out.print((i + 1) + ": ");
                    printTile(t);
                    out.print("\n");
                }
                for (int i = 0; i < order.length; i++) {
                    order[i] = readNumberFromInput(1, coords.size()) - 1;
                }
                // checks that the given order actually contains different numbers (no duplicates)
                if(Arrays.stream(order).collect(Collectors.toSet()).size() == order.length){
                    break;
                }
                out.println("Invalid order!");
            }

            int column = -1;

            while (column == -1) {
                out.print("If you want to redo the turn insert 0, otherwise select in which column you want to insert the selected tiles [1 - " + Shelf.getColumns() + "]: ");
                String input = in.nextLine();
                if (input == null || input.length() == 0 || !checkUserInput(0, Shelf.getColumns(), input.charAt(0) - '0')) {
                    out.println("Insert a valid input");
                    continue;
                }
                try {
                    column = Integer.parseInt(input) - 1;
                } catch (NumberFormatException e) {
                    out.println("Insert a valid input");
                    continue;
                }
                //in case the user wants to redo the turn
                if (column == -1) break;
                if (this.freeSpacesInMyShelf[column] < coords.size()) {
                    out.println("The chosen tiles can not fit in the column number " + (column + 1));
                    column = -1;
                    continue;
                }
                break;
            }

            if(column  == -1) continue;

            Coordinates[] chosenTiles = new Coordinates[coords.size()];
            for(int i = 0; i < coords.size(); i++){
                chosenTiles[i] = coords.get(order[i]);
            }

            // mark that the turn has been played
            currentPlayer = -1;
            return new TurnActionMessage(username, chosenTiles, column);
        }
    }

    @Override
    public void run() {
        clearConsole();
        showTitle();
        while(!chooseConnection());
        ClientImplementation.logger.log(Level.INFO,"Connected to server!");

        //noinspection InfiniteLoopStatement
        while(true){

            doLogin();

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    while(true){
                        if(isMessagesQueueEmpty()){
                            continue;
                        }
                        Message m = popMessageFromQueue();
                        ClientImplementation.logger.log(Level.INFO, "Inizio gestione messaggio: " + m.getClass());

                        if(m instanceof  LobbyMessage){
                            printPlayersInLobby((LobbyMessage) m);
                        } else if (m instanceof UpdateViewMessage) {
                            synchronized (lockMainThread){
                                update(((UpdateViewMessage) m).getGameView());
                                //this if ensures that the game is printed only when it is necessary
                                if(((UpdateViewMessage) m).getGameView().getCurrentPlayer() != null || ( ((UpdateViewMessage) m).getGameView().getPlayers() != null &&
                                        Arrays.stream(((UpdateViewMessage) m).getGameView().getPlayers()).filter(x -> x!=null).count() > 1 ) ) printTheWholeGame();
                                lockMainThread.notifyAll();
                            }

                        }
                        ClientImplementation.logger.log(Level.INFO, "Fine gestione messaggio: " + m.getClass());
                        if(gameEnded) break;
                    }
                }
            }.start();

            while(true){

                synchronized (lockMainThread){
                    while( (gameBoardView == null || currentPlayer == -1 || !players[currentPlayer].getUsername().equals(username)) && !gameEnded) {
                        try {
                            lockMainThread.wait();
                        } catch (InterruptedException e) {
                            System.err.println("Interrupted while waiting for server: " + e.getMessage());
                            ClientImplementation.logger.log(Level.SEVERE, e.getMessage(), e);
                            throw new RuntimeException(e);
                        }
                    }
                }
                if(gameEnded) break;
                notifyListeners(doTurnAction());
                ClientImplementation.logger.log(Level.INFO, "Sent TurnActionMessage to listeners");
            }

            out.println("Insert 0 to go back to the starting menù: ");
            readNumberFromInput(0,0);
            init();
            clearConsole();
            showTitle();
        }
    }

    /**
     * Clears the console.
     */
    public void clearConsole() {
        try{
            if( System.getProperty("os.name").contains("Windows") )
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                out.println("\033[H\033[2J");
        }catch (Exception ignored){
        }
    }


}