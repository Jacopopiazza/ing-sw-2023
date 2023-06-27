package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidIPAddress;
import it.polimi.ingsw.Exceptions.InvalidPort;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Model.Utilities.Config;
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
    private final int maxNumOfChosenTiles = 3;
    private GameBoardView gameBoardView;
    private PlayerView[] players;
    private String cheater;
    private int numOfActivePlayers;
    private int currentPlayer;
    private GlobalGoalView[] globalGoals;
    private boolean gameEnded;
    private Queue<Message> receivedMessages;

    // Write this title in a config file
    String r1 = " __    __           ______ _           _  __  _";
    String r2 = "|  \\  /  |         /  ____| |         | |/ _|(_)";
    String r3 = "| \\ \\/ / |_   _   |  (___ | |__   ___ | | |_  _  ___";
    String r4 = "| |\\__/| | | | |   \\___  \\|  _ \\ / _ \\| |  _|| |/ _ \\";
    String r5 = "| |    | | |_| |    ___)  | | | |  __/| | |  | |  __/";
    String r6 = "|_|    |_|\\__, |   |_____/|_| |_|\\___/|_|_|  |_|\\___/";
    String r7 = "            _/ |";
    String r8 = "           |__/";

    private final Object lockLogin;
    private final Object lockQueue;
    private final Object lockMainThread;

    public TextualUI() {
        super();
        in = new Scanner(System.in);
        out = new PrintStream(System.out, true);

        freeSpacesInMyShelf = new int[Shelf.getColumns()];
        gameEnded = false;

        receivedMessages = new LinkedList<>();
        lockLogin = new Object();
        lockQueue = new Object();
        lockMainThread = new Object();
    }

    @Override
    public void update(Message m){
        ClientImplementation.logger.log(Level.INFO,"CLI Received " + m.toString());

        addMessageToQueue(m);
        synchronized (lockLogin) {
            lockLogin.notifyAll();
        }
    }

    private boolean isMessagesQueueEmpty(){
        synchronized (lockQueue){
            return this.receivedMessages.isEmpty();
        }
    }

    private Message getFirstMessageFromQueue(){
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
        out.println(r1);
        out.println(r2);
        out.println(r3);
        out.println(r4);
        out.println(r5);
        out.println(r6);
        out.println(r7);
        out.println(r8);
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

    private int readChoiceFromInput(String option_1, String option_2){
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
            if(choice<1 || choice>2) out.println("Invalid selection!");
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

    public boolean chooseConnection(){
        out.println("Choose the connection type:");
        int choice = readChoiceFromInput("RMI","SOCKET");

        String ip;
        String port;
        do {
            out.println("Please provide the IP address of the server:");
            ip = in.nextLine();
        }while(!IPAddressValidator.isValidIPAddress(ip));
        do{
            out.println("Please provide the port of the " + (choice == 1 ? "RMI" : "Socket") + " server:");
            port = in.nextLine();
        }while(!IPAddressValidator.isValidPort(port));

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
        }while(this.username.contains(" "));

        ClientImplementation.logger.log(Level.INFO,"The user chose the username: " + this.username);
        doReconnect(this.username);
        waitForLoginResponse();
    }

    private void chooseLobby(){
        out.println("Choose to create a lobby or to join a lobby:");
        int choice = readChoiceFromInput("Create a new lobby","Join an existing lobby");

        switch (choice){
            case 1:
                askPlayerNumOfPlayerForLobby();
                break;
            case 2:
                doConnect(this.username, 1);
                break;
        }
        waitForLoginResponse();
    }

    private void askPlayerNumOfPlayerForLobby(){
        out.println("Insert the number of players for the game:");
        int numOfPlayers = readNumberFromInput(2,Config.getInstance().getMaxNumberOfPlayers());
        doConnect(this.username, numOfPlayers);
    }

    private void printPlayersInLobby(LobbyMessage m){
        out.println("Playes in lobby:");
        m.getPlayers().stream().forEach(x -> out.println(x));
    }

    private boolean doLogin() {

        readUsername();

        while(true)
        {
            Message m = this.getFirstMessageFromQueue();

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
            else{
                addMessageToQueue(m);
                waitForLoginResponse();
            }
        }

        return true;
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

        this.cheater = gv.getCheater(); // can be null if no-one cheated
        if(gv.getNumOfActivePlayers() != null) this.numOfActivePlayers = gv.getNumOfActivePlayers();
        if(gv.getGlobalGoals() != null) this.globalGoals = gv.getGlobalGoals();
        if(gv.getCurrentPlayer() != null) this.currentPlayer = gv.getCurrentPlayer();
    }

    private void printTheWholeGame(){
        // start the game
        int myIndex = 0;
        // get my player index
        for (int i = 0; i < this.players.length; i++) {
            if (this.players[i].getUsername().equals(username))
                myIndex = i;
        }

        showBoard();
        showGlobalGoals();
        showShelves();
        showPrivateGoal(this.players[myIndex].getPrivateGoal().getCoordinates());
        if(cheater != null){
            out.println(cheater + " tried to cheat!");
            cheater = null;
        }
        if(currentPlayer != -1)
            out.println("It is "+ ( players[currentPlayer].getUsername().equals(username)? "your" : ( players[currentPlayer].getUsername() + "'s" ) ) + " turn" );
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

    public void showBoard(){
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

    public void showPrivateGoal(Coordinates[] privateGoal){
        out.println("\n\n\tMy private goals\n\n");
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

    public void showShelves(){
        int r = Shelf.getRows();
        int c = Shelf.getColumns();

        TileView tile;
        out.println("\n\n\n");

        // display names and scores
        out.print("\t");
        for(int i = 0; i < players.length; i++){
            out.print( ( players[i].getUsername().equals(username) ? "My" : (players[i].getUsername() + "'s") ) + " Shelf [" + players[i].getScore() + "]\t\t\t\t");
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
            out.println((i + 1) + globalGoals[i].getDescription() + "[" + globalGoals[i].getCurrentScore() + "]");
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
                while(true){
                    //checks that one more tile can be picked
                    if(coords.size() == maxNumOfChosenTiles ){
                        out.println("You can not select more than " + String.valueOf(maxNumOfChosenTiles) + " tiles" );
                        break;
                    }
                    //checks that one more tile can fit in the shelf
                    if(maxFreeSpacesInMyShelf == coords.size()){
                        out.println("In your shelf there is space for at most " + String.valueOf(maxFreeSpacesInMyShelf) + " tiles");
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
                                "and a column [0 - " + numCols +"]");
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
                    break;
                }
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

            // Once I have the Tiles to pick, print those Tiles and ask for their order
            out.println("Insert the numbers corresponding to the tiles in the order according to which you want them:");
            for (int i = 0; i < coords.size(); i++) {
                TileView t = gameBoardView.getTile(coords.get(i));
                out.print((i + 1) + ": ");
                printTile(t);
                out.print("\n");
            }

            int[] order = new int[coords.size()];
            for (int i = 0; i < order.length; i++) {
                order[i] = readNumberFromInput(1, coords.size()) - 1;
            }

            int column = -1;

            while (column == -1) {
                out.print("If you want to redo the turn insert 0, otherwise select in which column you want to insert the selected tiles [1 - " + Shelf.getColumns() + "]: ");
                String input = in.nextLine();
                if (!checkUserInput(0, Shelf.getColumns(), input.charAt(0) - '0')) {
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
                    out.println("The chosen tiles can not fit in the column number " + String.valueOf(column + 1));
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
        showTitle();
        while(!chooseConnection());
        ClientImplementation.logger.log(Level.INFO,"Connected to server!");

        while(!doLogin());

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
                        synchronized (lockMainThread){
                            update(((UpdateViewMessage) m).getGameView());
                            printTheWholeGame();
                            lockMainThread.notifyAll();
                        }

                    }
                    ClientImplementation.logger.log(Level.INFO, "Fine gestione messaggio: " + m.getClass());
                }
            }
        }.start();

        while(!gameEnded){

            synchronized (lockMainThread){
                while(gameBoardView == null || currentPlayer == -1 || !players[currentPlayer].getUsername().equals(username)) {
                    try {
                        lockMainThread.wait();
                    } catch (InterruptedException e) {
                        System.err.println("Interrupted while waiting for server: " + e.getMessage());
                        ClientImplementation.logger.log(Level.SEVERE, e.getMessage(), e);
                        throw new RuntimeException(e);
                    }
                }
            }
            notifyListeners(doTurnAction());
            ClientImplementation.logger.log(Level.INFO, "Sent TurnActionMessage to listeners");
        }
    }

    public final static void clearConsole() {
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