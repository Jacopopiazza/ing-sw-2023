package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.AppClientImplementation;
import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.Model.Utilities.Config;
import it.polimi.ingsw.ModelView.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;
import java.util.List;

public class GraphicalUI extends ClientManager {

    private String username;
    private StartWindow startWindow = null;
    private GameWindow gameWindow = null;

    private class Background extends JPanel {
        private Image backgroundImage;

        private Background(String imagePath){
            super();
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        private Background(Image image){
            super();
            backgroundImage = image;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private class ImageManager {
        private static final List<ImageIcon>[] tilesIcons = new ArrayList[TileColor.values().length];
        private static final ImageIcon goldenRing = new ImageIcon("visual_components/item tiles/bordo oro.png");

        private static ImageIcon getTileImage(TileColor color,int id,boolean pickable){
            //initialize the container of the images
            if(tilesIcons[0] == null){
                for(TileColor tc : TileColor.values()){
                    tilesIcons[tc.ordinal()] = new ArrayList<>();
                    for(int i=0; i<3; i++){
                        tilesIcons[tc.ordinal()].add(new ImageIcon("visual_components/item tiles/"+tc.name().toLowerCase()+String.valueOf(i+1)+".png"));
                    }
                }
            }

            //take the selected tile
            ImageIcon tile = null;
            for(TileColor tc : TileColor.values()){
                if(tc.equals(color)) tile = tilesIcons[tc.ordinal()].get(id);
            }
            if(tile == null) return null;
            if(!pickable) return tile;

            //add the golden ring to the tile
            BufferedImage layeredImage = new BufferedImage(tile.getIconWidth(), tile.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = layeredImage.createGraphics();
            g2d.drawImage(tile.getImage(), 0, 0, null);
            g2d.drawImage(goldenRing.getImage(), 0, 0, null);
            g2d.dispose();
            return new ImageIcon(layeredImage);
        }

        private static ImageIcon resizeImageIcon(ImageIcon icon, int width, int height){
            Image img = icon.getImage();
            Image newimg = img.getScaledInstance( width, height,  java.awt.Image.SCALE_SMOOTH ) ;
            return new ImageIcon( newimg );
        }

        private static ImageIcon rotateImageIcon(ImageIcon imageIcon, double degrees) {
            Image image = imageIcon.getImage();
            int width = image.getWidth(null);
            int height = image.getHeight(null);

            double radians = Math.toRadians(degrees);
            double sin = Math.abs(Math.sin(radians));
            double cos = Math.abs(Math.cos(radians));

            int newWidth = (int) Math.round(width * cos + height * sin);
            int newHeight = (int) Math.round(height * cos + width * sin);

            BufferedImage rotatedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = rotatedImage.createGraphics();

            AffineTransform transform = new AffineTransform();
            transform.translate((newWidth - width) / 2, (newHeight - height) / 2);
            transform.rotate(radians, width / 2, height / 2);
            g2d.drawImage(image, transform, null);
            g2d.dispose();


            return new ImageIcon(rotatedImage);
        }

        private static JLabel getVoidLabel(){
            JLabel label = new JLabel();
            label.setOpaque(false);
            return label;
        }

        private static JLabel getTileLabel(TileView tile, int width, int height){
            ImageIcon ic = ImageManager.getTileImage(tile.getCOLOR(),tile.getID()%3,false);
            return new JLabel(ImageManager.resizeImageIcon(ic,width,height));
        }

    }

    private class StartWindow extends JFrame{
        private JPanel request;
        private JLabel error;

        private StartWindow(){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1120, 630); // 16:9 proportion
            setLocationRelativeTo(null);

            //create the background
            JPanel background = new Background("visual_components/misc/base_pagina2.jpg");
            background.setLayout(new BorderLayout());

            //create the content
            JPanel content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content,BoxLayout.PAGE_AXIS));
            content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            //add the title
            Image titleImage = new ImageIcon("visual_components/Publisher material/Title 2000x618px.png").getImage();
            ImageIcon titleIcon = new ImageIcon(titleImage.getScaledInstance(1000,309,Image.SCALE_DEFAULT));
            JLabel title = new JLabel(titleIcon);
            title.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            content.add(title);

            //add the request
            request = new JPanel();
            request.setOpaque(false);
            request.setLayout(new BoxLayout(request,BoxLayout.PAGE_AXIS));
            error = new JLabel();
            askConnection();
            content.add(request);

            //add the error
            error.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            error.setForeground(Color.decode("14929049"));
            content.add(error);

            background.add(content,BorderLayout.CENTER);
            JScrollPane scrollPane = new JScrollPane(background);
            scrollPane.setPreferredSize(new Dimension(1120, 630));
            scrollPane.setOpaque(false);
            add(scrollPane);

            setVisible(true);
        }

        private void askConnection(){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("Do you want to use RMI or Socket?"));

            //set up the options
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton rmi = getStandardButton("RMI");
            rmi.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(connectionChosen(button)) {
                        askUsername();
                    }
                    else{
                        error.setText("Unable to connect with RMI");
                    }
                }
            });
            JButton socket = getStandardButton("Socket");
            socket.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(connectionChosen(button)){
                        askUsername();
                    }
                    else{
                        error.setText("Unable to connect with Socket");
                    }
                }
            });
            choices.add(rmi);
            choices.add(socket);
            request.add(choices);
            request.revalidate();
            request.repaint();
        }

        private void askUsername(){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("Insert the username"));

            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new FlowLayout());

            //set up the username text box
            JTextField inputText = new JTextField();
            inputText.setPreferredSize(new Dimension(450,40));
            inputText.setFont(inputText.getFont().deriveFont(20f));
            inputText.setBackground(Color.decode("14929049"));
            inputText.setForeground(Color.decode("5776384"));

            //set up the submit button
            JButton submit = getStandardButton("Submit");
            submit.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    username = inputText.getText();
                    inputText.setText("");
                    if (username.contains(" ")){
                        error.setText("Spaces are not allowed in the username");
                        username = null;
                    }
                    else doReconnect(username);
                }
            });

            panel.add(inputText);
            panel.add(submit);
            request.add(panel);
            request.revalidate();
            request.repaint();
        }

        private void askLobby(){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("Do you want to create a lobby or to join a lobby?"));

            //set up the options
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton create = getStandardButton("Create");
            create.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(button.getText().equals("Create")) {
                        askNumOfPlayersInLobby();
                    }
                }
            });
            JButton join = getStandardButton("Join");
            join.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(button.getText().equals("Join")) {
                        doConnect(username,1);
                    }
                }
            });
            choices.add(create);
            choices.add(join);
            request.add(choices);
            request.revalidate();
            request.repaint();
        }

        private void askNumOfPlayersInLobby(){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("How many players do you want in your lobby?"));

            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new FlowLayout());

            //set up the options
            for(int i = 2; i <= Config.getInstance().getMaxNumberOfPlayers();i++){
                JButton choice = getStandardButton(String.valueOf(i));
                choice.addActionListener((e) -> {
                    if(e.getSource() instanceof JButton button){
                        int numOfPlayers = Integer.parseInt(button.getText());
                        if(numOfPlayers > 1 && numOfPlayers <= Config.getInstance().getMaxNumberOfPlayers()) {
                            doConnect(username,numOfPlayers);
                        }
                    }
                });
                panel.add(choice);
            }

            request.add(panel);
            request.revalidate();
            request.repaint();
        }

        private void showLobby(List<String> players){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("players in lobby:"));
            for(String p : players) request.add(getStandardText(p));

            //set up the exit button
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            JButton exit = getStandardButton("Exit");
            exit.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(button.getText().equals("Exit")) {
                        doQuit(username);
                        username = null;
                        askUsername();
                    }
                }
            });

            panel.add(exit);
            request.add(panel);
            request.revalidate();
            request.repaint();
        }

        private JButton getStandardButton(String name){
            JButton button = new JButton(name);
            button.setPreferredSize(new Dimension(100,40));
            button.setBackground(Color.decode("14929049"));
            button.setForeground(Color.decode("5776384"));
            button.setFont(button.getFont().deriveFont(18f));
            return button;
        }

        private JLabel getStandardText(String text){
            JLabel label = new JLabel(text);
            label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            label.setFont(label.getFont().deriveFont(20f));
            label.setForeground(Color.decode("14929049"));
            return label;
        }

        private boolean connectionChosen (JButton button) {
            if(button.getText().equals("RMI")){
                try{
                    setUpRMIClient();
                    return true;
                }catch (RemoteException | NotBoundException ex ){
                    return false;
                }
            } else if (button.getText().equals("Socket")) {
                try{
                    setUpSocketClient();
                    return true;
                }catch (RemoteException | NotBoundException ex ){
                    return false;
                }
            }
            return false;
        }

    }

    private class GameWindow extends JFrame{
        private class GameBoardPanel extends Background {
            private final static ImageIcon victoryToken = new ImageIcon("visual_components/scoring tokens/end game.jpg");
            private final static ImageIcon boardIcon = new ImageIcon("visual_components/boards/livingroom.png");
            private int width;
            private int height;
            private static final int gameBoardDim = 9;
            private boolean isFinished;
            private int numberOfPicks;

            private GameBoardPanel(GameBoardView gameBoard, int width, int height, boolean isFinished){
                super(boardIcon.getImage());
                this.isFinished = isFinished;
                this.width = width;
                this.height = height;
                setToolTipText("Board");
                setPreferredSize(new Dimension(width,height));
                setBorder(BorderFactory.createEmptyBorder((int)(height/24), (int)(width/23.23), (int)(height/24), (int)(width/19.46)));
                setLayout(new GridLayout(gameBoardDim, gameBoardDim, (int)(width/144), (int)(height/144)));

                Set<Coordinates> coordinatesSet = gameBoard.getCoords();
                Coordinates coords;
                numberOfPicks = 0;
                for (int i = 0; i < gameBoardDim; i++) {
                    for (int j = 0; j < gameBoardDim; j++) {
                        coords = new Coordinates(i,j);
                        if(coordinatesSet.contains(coords) && gameBoard.getTile(coords) != null){
                            if(gameBoard.isPickable(coords)) add(getTileButton(gameBoard.getTile(coords), i, j));
                            else add(ImageManager.getTileLabel( gameBoard.getTile(coords),(int)(width/10.59),(int)(height/10.59) ));
                        }else{
                            add(ImageManager.getVoidLabel());
                        }
                    }
                }
            }

            private JButton getTileButton(TileView tile, int x, int y){
                ImageIcon icon = ImageManager.getTileImage(tile.getCOLOR(),tile.getID()%3,true);
                JButton button = new JButton(ImageManager.resizeImageIcon(icon,(int)(width/10.59),(int)(height/10.59)));
                button.setBorderPainted(false);
                button.setOpaque(false);
                button.addActionListener((e) -> {
                    if(e.getSource() instanceof JButton pressed){
                        // on every pick check that this tile is on the same line
                        if(numberOfPicks < 3) {
                            numberOfPicks++;
                            System.out.println("Pressed " + x + "-" + y);
                        }else{
                            System.out.println("Max number of tiles picked!");
                        }
                    }
                });
                return button;
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(isFinished) return;
                ImageIcon token = ImageManager.rotateImageIcon(victoryToken,9.5);
                token = ImageManager.resizeImageIcon(token,(int)(width/9.5),(int)(height/9.5));
                g.drawImage(token.getImage(),(int)(width/1.235),(int)(height/1.434),this);
            }
        }

        private class ShelfPanel extends Background {
            private static final int rows = Shelf.getRows();
            private static final int cols = Shelf.getColumns();

            private ShelfPanel(ShelfView shelfView, String imagePath, String toolTip, int width, int height){
                super(imagePath);
                setOpaque(false);
                setToolTipText(toolTip);
                setLayout(new BorderLayout());
                setPreferredSize(new Dimension(width,height));
                setLayout(new GridLayout(rows, cols, (int)(width/25), (int)(height/62.5)));
                setBorder(BorderFactory.createEmptyBorder((int)(height/15.625), (int)(width/8.33),
                        (int)(height/8.77), (int)(width/8.77)));

                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if(shelfView.getTile(new Coordinates(i,j)) != null) add(ImageManager.getTileLabel( shelfView.getTile(new Coordinates(i,j)),(int)(width/7.35),(int)(height/7.35) ));
                        else add(ImageManager.getVoidLabel());
                    }
                }
            }

        }

        private class PrivateGoalPanel extends JLabel {
            private PrivateGoalPanel(int pvtGoalIndex, int width, int height){
                super(ImageManager.resizeImageIcon(new ImageIcon("visual_components/personal goal cards/Personal_Goals" + pvtGoalIndex + ".png"),width,height));
                setToolTipText("Private goal");
                setPreferredSize(new Dimension(width,height));
            }

        }

        private class GlobalGoalPanel extends Background {
            private ImageIcon scoreIcon;
            private int width;
            private int height;
            private GlobalGoalPanel(GlobalGoalView ggv, int width, int height){
                super("visual_components/common goal cards/" + ggv.getId() + ".jpg");
                this.width = width;
                this.height = height;
                setPreferredSize(new Dimension(this.width, this.height));
                setOpaque(false);
                setToolTipText(ggv.getDescription());

                // set scoreIcon
                if(ggv.getCurrentScore() > 0) scoreIcon = new ImageIcon("visual_components/scoring tokens/scoring_" + ggv.getCurrentScore() + ".jpg");
                else scoreIcon = null;
            }

            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                if(scoreIcon != null){
                    scoreIcon = ImageManager.resizeImageIcon(scoreIcon, (int) (width/3.675), (int) (height/2.45));
                    scoreIcon = ImageManager.rotateImageIcon(scoreIcon, -7);
                    g.drawImage(scoreIcon.getImage(), (int)(width/1.65), (int)(height/3.8), this);
                }
            }

        }

        private GameBoardPanel gameBoardPanel;
        private ShelfPanel[] shelves;
        private PrivateGoalPanel privateGoalPanel;
        private GlobalGoalPanel[] globalGoalPanel;
        private GameWindow(GameView gameView){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1280, 720); // 16:9
            setLocationRelativeTo(null);    // in the middle of the screen

            // set up the background
            JPanel background = new Background("visual_components/misc/sfondo parquet.jpg");
            background.setLayout(new BoxLayout(background,BoxLayout.PAGE_AXIS));
            JPanel upperPanel = new JPanel();
            upperPanel.setOpaque(false);
            upperPanel.setLayout(new FlowLayout());
            background.add(upperPanel);
            JPanel lowerPanel = new JPanel();
            lowerPanel.setOpaque(false);
            lowerPanel.setLayout(new FlowLayout());
            background.add(lowerPanel);
            JScrollPane scrollPane = new JScrollPane(background);
            scrollPane.setOpaque(false);
            scrollPane.setPreferredSize(new Dimension(1280,720));
            add(scrollPane);

            //creating the shelfPanel Panel
            shelves = new ShelfPanel[gameView.getPlayers().length];
            JPanel shelvesPanel = new JPanel();
            shelvesPanel.setOpaque(false);
            shelvesPanel.setLayout(new BoxLayout(shelvesPanel,BoxLayout.PAGE_AXIS));
            upperPanel.add(shelvesPanel);
            for(int i = 0; i<gameView.getPlayers().length;i++){
                PlayerView p = gameView.getPlayers()[i];
                if(p.getUsername().equals(username)){
                    shelves[i] = new ShelfPanel(p.getShelf(),"visual_components/boards/bookshelf.png","My shelf",500,500);
                    privateGoalPanel = new PrivateGoalPanel(p.getPrivateGoal().getId(), 150, 225);
                    lowerPanel.add(shelves[i]);
                    lowerPanel.add(privateGoalPanel);
                }
                else{
                    shelves[i] = new ShelfPanel(p.getShelf(),"visual_components/boards/bookshelf_orth.png",p.getUsername()+"'s shelf",225,225);
                    shelvesPanel.add(shelves[i]);
                }
            }

            //creating the gameBoard Panel
            gameBoardPanel = new GameBoardPanel(gameView.getGameBoard(),700,700,false);
            upperPanel.add(gameBoardPanel);


            // creating the Panel containing private and global Goals
            JPanel goals = new JPanel();
            goals.setOpaque(false);
            goals.setLayout(new BoxLayout(goals, BoxLayout.PAGE_AXIS));
            upperPanel.add(goals);
            // adding the content
            globalGoalPanel = new GlobalGoalPanel[gameView.getGlobalGoals().length];
            for(int i=0; i<gameView.getGlobalGoals().length;i++){
                globalGoalPanel[i] = new GlobalGoalPanel(gameView.getGlobalGoals()[i],225,150 );
                goals.add(globalGoalPanel[i]);
            }

            pack();
            setVisible(true);
        }

        private void update(GameView gw){

        }
    }



    @Override
    public void update(Message m) {
        AppClientImplementation.logger.log(Level.INFO,"GUI Received message");

        if(m instanceof NoUsernameToReconnectMessage){
            startWindow.askLobby();
        }
        else if(m instanceof TakenUsernameMessage){
            username = null;
            startWindow.askUsername();
            startWindow.error.setText("Username is already taken");
        }
        else if(m instanceof NoLobbyAvailableMessage){
            startWindow.error.setText("There are no lobbies available at the moment, create a new one");
        }
        else if(m instanceof LobbyMessage){
            startWindow.showLobby(((LobbyMessage) m).getPlayers());
        }
        else if(m instanceof GameServerMessage){
            cleanListeners();
            addListener((message) -> {
                try{
                    ((GameServerMessage) m).getServer().handleMessage(message,client);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            });
        }
        else if(m instanceof UpdateViewMessage){
            if(gameWindow == null){
                startWindow.dispose();
                gameWindow = new GameWindow(((UpdateViewMessage)m).getGameView());
            }
            else gameWindow.update(((UpdateViewMessage)m).getGameView());
        }
        else{
            AppClientImplementation.logger.log(Level.INFO,"CLI: received message from server of type: " + m.getClass().getSimpleName() + " , but no notify has been implemented for this type of message");
        }
    }

    public void run(){
        startWindow = new StartWindow();
    }

}
