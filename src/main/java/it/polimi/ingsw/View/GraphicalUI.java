package it.polimi.ingsw.View;

import it.polimi.ingsw.Exceptions.InvalidIPAddress;
import it.polimi.ingsw.Exceptions.InvalidPort;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.Model.Utilities.Config;
import it.polimi.ingsw.ModelView.*;
import it.polimi.ingsw.Network.ClientImplementation;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.List;

public class GraphicalUI extends UserInterface {

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
            request.add(getStandardLabel("Set up the connection"));

            //set up the text fields for server IP and Port
            JPanel wrapper = new JPanel();
            wrapper.setOpaque(false);
            wrapper.setLayout(new FlowLayout());
            wrapper.setMaximumSize(new Dimension(450,60));
            request.add(wrapper);

            JPanel ipPanel = new JPanel();
            ipPanel.setOpaque(false);
            ipPanel.setLayout(new BoxLayout(ipPanel,BoxLayout.PAGE_AXIS));
            wrapper.add(ipPanel);
            JLabel ip = new JLabel("IP:");
            ip.setFont(ip.getFont().deriveFont(14f));
            ip.setForeground(Color.decode("14929049"));
            ipPanel.add(ip);
            JTextField ipTextField = getStandardTextField(150,25,14f);
            ipPanel.add(ipTextField);

            JPanel portPanel = new JPanel();
            portPanel.setOpaque(false);
            portPanel.setLayout(new BoxLayout(portPanel,BoxLayout.PAGE_AXIS));
            wrapper.add(portPanel);
            JLabel port = new JLabel("Port:");
            port.setFont(port.getFont().deriveFont(14f));
            port.setForeground(Color.decode("14929049"));
            portPanel.add(port);
            JTextField portTextField = getStandardTextField(150,25,14f);
            portPanel.add(portTextField);

            //set up the options
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton rmi = getStandardButton("RMI");
            rmi.setPreferredSize(new Dimension(150,40));
            rmi.addActionListener((e) -> {
                if(e.getSource() instanceof JButton){
                    try{
                        setUpRMIClient(ipTextField.getText(),portTextField.getText());
                    }catch (RemoteException | NotBoundException ex ){
                        error.setText("Unable to connect with RMI");
                        return;
                    } catch (InvalidIPAddress ex) {
                        error.setText("IP not found");
                        return;
                    } catch (InvalidPort ex) {
                        error.setText("Port not valid");
                        return;
                    }
                    askUsername();
                }
            });
            JButton socket = getStandardButton("Socket");
            socket.setPreferredSize(new Dimension(150,40));
            socket.addActionListener((e) -> {
                if(e.getSource() instanceof JButton){
                    try{
                        setUpSocketClient(ipTextField.getText(),portTextField.getText());
                    }catch (RemoteException | NotBoundException ex ){
                        error.setText("Unable to connect with Socket");
                        return;
                    } catch (InvalidIPAddress ex) {
                        error.setText("IP not found");
                        return;
                    } catch (InvalidPort ex) {
                        error.setText("Port not valid");
                        return;
                    }
                    askUsername();
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
            request.add(getStandardLabel("Insert the username"));

            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new FlowLayout());

            //set up the username text field
            JTextField inputText = getStandardTextField(450,40,20f);

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
            request.add(getStandardLabel("Do you want to create a lobby or to join a lobby?"));

            //set up the options
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton create = getStandardButton("Create");
            create.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    askNumOfPlayersInLobby();
                }
            });
            JButton join = getStandardButton("Join");
            join.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    doConnect(username,1);
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
            request.add(getStandardLabel("How many players do you want in your lobby?"));

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
            request.add(getStandardLabel("players in lobby:"));
            for(String p : players) request.add(getStandardLabel(p));

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

        private JLabel getStandardLabel(String text){
            JLabel label = new JLabel(text);
            label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            label.setFont(label.getFont().deriveFont(20f));
            label.setForeground(Color.decode("14929049"));
            return label;
        }

        private JTextField getStandardTextField(int width, int height, float font){
            JTextField textField = new JTextField();
            textField.setPreferredSize(new Dimension(width,height));
            textField.setFont(textField.getFont().deriveFont(font));
            textField.setBackground(Color.decode("14929049"));
            textField.setForeground(Color.decode("5776384"));
            return textField;
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

            private GameBoardPanel(GameBoardView gameBoard, int width, int height){
                super(boardIcon.getImage());
                this.width = width;
                this.height = height;
                setToolTipText("Board");
                setPreferredSize(new Dimension(width,height));
                setBorder(BorderFactory.createEmptyBorder((int)(height/24), (int)(width/23.23), (int)(height/24), (int)(width/19.46)));
                setLayout(new GridLayout(gameBoardDim, gameBoardDim, (int)(width/144), (int)(height/144)));

                update(gameBoard);
            }

            private JButton getTileButton(TileView tile, int row, int col){
                ImageIcon icon = ImageManager.getTileImage(tile.getCOLOR(),tile.getID()%3,true);
                JButton button = new JButton(ImageManager.resizeImageIcon(icon,(int)(width/10.59),(int)(height/10.59)));
                button.setBorderPainted(false);
                button.setOpaque(false);
                button.addActionListener((e) -> {
                    errorText.setText("");
                    if(e.getSource() instanceof JButton && !isGameFinished){
                        //checks that is my turn
                        if(myId != currentPlayer){
                            errorText.setText("It is not your turn");
                            return;
                        }
                        //checks that one more tile can be picked
                        if(chosenTiles[chosenTiles.length - 1] != null){
                            errorText.setText("You can not select more than " + String.valueOf(chosenTiles.length) + " tiles" );
                            return;
                        }
                        //checks that one more tile can fit in the shelf
                        if(maxFreeSpacesInMyShelf < chosenTiles.length && chosenTiles[maxFreeSpacesInMyShelf - 1] != null){
                            errorText.setText("In your shelf there is space for at most " + String.valueOf(maxFreeSpacesInMyShelf) + " tiles");
                            return;
                        }
                        //checks that this tile has not been already picked and that is next to one of the previously picked ones
                        boolean nextTo = false;
                        int i;
                        for(i = 0;i<chosenTiles.length && chosenTiles[i]!=null;i++){
                            if(chosenTiles[i].getROW() == row && chosenTiles[i].getCOL() == col){
                                errorText.setText("You have already selected this tile");
                                return;
                            }
                            if(( ( chosenTiles[i].getCOL()+1 == col || chosenTiles[i].getCOL()-1 == col ) && chosenTiles[i].getROW() == row ) || ( ( chosenTiles[i].getROW()+1 == row || chosenTiles[i].getROW()-1 == row ) && chosenTiles[i].getCOL() == col )) nextTo=true;
                        }
                        if(!nextTo && chosenTiles[0] != null){
                            errorText.setText("This tile is not next to one of the others you selected");
                            return;
                        }
                        //checks that this tile is on the same column or on the same row with the previously picked ones
                        boolean sameRow = true;
                        boolean sameColumn = true;
                        for(i = 0; i< chosenTiles.length-1 && chosenTiles[i+1]!=null && (sameRow || sameColumn); i++) {
                            if(chosenTiles[i].getROW() != chosenTiles[i+1].getROW()) sameRow = false;
                            if(chosenTiles[i].getCOL() != chosenTiles[i+1].getCOL()) sameColumn = false;
                        }
                        if( !(sameRow && sameColumn) ){
                            if( (sameRow && chosenTiles[0].getROW() != row) || (sameColumn && chosenTiles[0].getCOL() != col)){
                                errorText.setText("The selected tiles must be on the same line");
                                return;
                            }
                        }
                        //all the checks are done, the selected tile is valid
                        for(i = 0; i<chosenTiles.length && chosenTiles[i]!=null;i++);
                        chosenTiles[i] = new Coordinates(row,col);
                        int myOrderId = i;
                        if(i == chosenTiles.length-1 || i == maxFreeSpacesInMyShelf -1) text.setText("You can not select more tiles, reorder the selected ones if you want and choose a column to end your turn");
                        else {
                            if (i == 0) text.setText("select another tile or choose a column to end your turn");
                            else text.setText("select another tile, reorder the selected ones or choose a column to end your turn");
                        }
                        for(i = 0; i<chosenOrder.length && chosenOrder[i]!=null;i++);
                        chosenOrder[i] = myOrderId;

                        //show the button to restart the turn
                        if(myOrderId == 0){
                            JButton restart = new JButton("Redo");
                            restart.setOpaque(false);
                            restart.setBorderPainted(false);
                            restart.setContentAreaFilled(false);
                            restart.addActionListener((e1) -> {
                                errorText.setText("");
                                if (e1.getSource() instanceof JButton && !isGameFinished) {
                                    for (int j = 0; j < chosenOrder.length && chosenOrder[j] != null; j++) {
                                        chosenTiles[chosenOrder[j]] = null;
                                        orderNumbers[chosenOrder[j]] = null;
                                        chosenOrder[j] = null;
                                    }
                                    columnChoicePanel.removeAll();
                                    columnChoicePanel.revalidate();
                                    columnChoicePanel.repaint();
                                    pickedTilesPanel.removeAll();
                                    pickedTilesPanel.revalidate();
                                    pickedTilesPanel.repaint();
                                    text.setText("Choose your tiles from the board");
                                }
                            });
                            pickedTilesPanel.add(restart);
                        }

                        //create the panel for the tile and its order number
                        JPanel panel = new JPanel();
                        panel.setOpaque(false);
                        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
                        pickedTilesPanel.add(panel);

                        //create the JLabel for the order number
                        orderNumbers[myOrderId] = new JLabel(String.valueOf(i+1)){
                            @Override
                            public void setText(String s){
                                super.setText(s);
                                revalidate();
                                repaint();
                            }
                        };
                        orderNumbers[myOrderId].setOpaque(false);
                        orderNumbers[myOrderId].setAlignmentX(JLabel.CENTER_ALIGNMENT);
                        orderNumbers[myOrderId].setFont(orderNumbers[myOrderId].getFont().deriveFont(14f));
                        JPanel numberWrapper = new JPanel();
                        numberWrapper.setOpaque(false);
                        numberWrapper.setLayout(new FlowLayout());
                        numberWrapper.add(orderNumbers[myOrderId]);

                        //create the order button
                        ImageIcon orderButtonIcon = ImageManager.getTileImage(tile.getCOLOR(),tile.getID()%3,false);
                        JButton orderButton =  new JButton(ImageManager.resizeImageIcon(orderButtonIcon,(int)(width/10.59),(int)(height/10.59)));
                        orderButton.setPreferredSize(new Dimension((int)(width/10.59),(int)(height/10.59)));
                        orderButton.setBorderPainted(false);
                        orderButton.setOpaque(false);
                        orderButton.addActionListener((e2) -> {
                            errorText.setText("");
                            if(e2.getSource() instanceof JButton && !isGameFinished){
                                //checks that is my turn
                                if(myId != currentPlayer){
                                    errorText.setText("It is not your turn");
                                    return;
                                }
                                for(int j=0;j<chosenOrder.length && chosenOrder[j]!=null;j++){
                                    if(chosenOrder[j] == myOrderId){
                                        int k;
                                        for(k=j;k<chosenOrder.length-1;k++){
                                            chosenOrder[k] = chosenOrder[k+1];
                                            if(chosenOrder[k] != null) orderNumbers[chosenOrder[k]].setText(String.valueOf(k+1));
                                            else break;
                                        }
                                        chosenOrder[k] = myOrderId;
                                        orderNumbers[myOrderId].setText(String.valueOf(k+1));
                                        break;
                                    }
                                }
                            }
                        });
                        JPanel buttonWrapper = new JPanel();
                        buttonWrapper.setOpaque(false);
                        buttonWrapper.setLayout(new FlowLayout());
                        buttonWrapper.add(orderButton);

                        //add order button and number to the panel
                        panel.add(buttonWrapper);
                        panel.add(numberWrapper);
                        if(myOrderId == 0) showColumnChoiceButtons();
                    }
                });
                return button;
            }

            private void update(GameBoardView gameBoard){
                removeAll();
                Set<Coordinates> coordinatesSet = gameBoard.getCoords();
                Coordinates coords;
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

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(isGameFinished) return;
                ImageIcon token = ImageManager.rotateImageIcon(victoryToken,9.5);
                token = ImageManager.resizeImageIcon(token,(int)(width/9.5),(int)(height/9.5));
                g.drawImage(token.getImage(),(int)(width/1.235),(int)(height/1.434),this);
            }
        }

        private class ShelfPanel extends Background {
            private static final int rows = Shelf.getRows();
            private static final int cols = Shelf.getColumns();
            private static final String imagePath = "visual_components/boards/bookshelf_orth.png";
            private int width;
            private int height;

            private ShelfPanel(PlayerView playerView, String toolTip, int width, int height){
                super(imagePath);
                this.width = width;
                this.height = height;
                setOpaque(false);
                setToolTipText(toolTip);
                setLayout(new BorderLayout());
                setPreferredSize(new Dimension(width,height));
                setLayout(new GridLayout(rows, cols, (int)(width/26.5), (int)(height/62.5)));
                setBorder(BorderFactory.createEmptyBorder((int)(height/15.625), (int)(width/8.33),
                        (int)(height/8.77), (int)(width/8.77)));
                update(playerView.getShelf(), playerView.getUsername().equals(username));
            }

            private void update(ShelfView shelfView,boolean mine){
                removeAll();
                if(mine){
                    for(int i = 0; i< freeSpacesInMyShelf.length; i++) freeSpacesInMyShelf[i] = 0;
                }
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if(shelfView.getTile(new Coordinates(i,j)) != null) {
                            add(ImageManager.getTileLabel( shelfView.getTile(new Coordinates(i,j)),(int)(width/7.35),(int)(height/7.35) ));
                        }
                        else{
                            add(ImageManager.getVoidLabel());
                            if(mine) freeSpacesInMyShelf[j]++;
                        }
                    }
                }
                if(mine){
                    maxFreeSpacesInMyShelf = 0;
                    for(int i = 0; i< freeSpacesInMyShelf.length; i++) if(freeSpacesInMyShelf[i] > maxFreeSpacesInMyShelf) maxFreeSpacesInMyShelf = freeSpacesInMyShelf[i];
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
                if(ggv.getCurrentScore() > 0) scoreIcon = new ImageIcon("visual_components/scoring tokens/scoring_" + ggv.getCurrentScore() + ".jpg");
                else scoreIcon = null;
                setPreferredSize(new Dimension(this.width, this.height));
                setOpaque(false);
                setToolTipText(ggv.getDescription());
            }

            @Override
            protected void paintComponent(Graphics g){
                super.paintComponent(g);
                if(scoreIcon != null){
                    ImageIcon token = ImageManager.rotateImageIcon(scoreIcon, -7);
                    token = ImageManager.resizeImageIcon(token, (int) (width/3.675), (int) (height/2.45));
                    g.drawImage(token.getImage(), (int)(width/1.65), (int)(height/3.8), this);
                }
            }

            private void update(GlobalGoalView ggv){
                if(ggv.getCurrentScore() == 0) scoreIcon = null;
                else scoreIcon = new ImageIcon("visual_components/scoring tokens/scoring_" + ggv.getCurrentScore() + ".jpg");
            }
        }

        //attributes related to game
        private int currentPlayer;
        private int myId;
        private int numOfActivePlayers;
        private boolean isGameFinished;
        int maxFreeSpacesInMyShelf;
        private int[] freeSpacesInMyShelf;
        private int maxNumOfChosenTiles = 3;
        private Coordinates[] chosenTiles;
        private Integer[] chosenOrder;
        private JLabel[] orderNumbers;

        //attributes related to the view
        private GameBoardPanel gameBoardPanel;
        private ShelfPanel[] shelves;
        private JLabel[] scores;
        private GlobalGoalPanel[] globalGoalPanel;
        private JLabel text;
        private JLabel errorText;
        private JPanel pickedTilesPanel;
        private JPanel columnChoicePanel;
        private GameWindow(GameView gameView){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1280, 720); // 16:9
            setLocationRelativeTo(null);    // in the middle of the screen

            //initialize private parameters
            currentPlayer = gameView.getCurrentPlayer();
            freeSpacesInMyShelf = new int[Shelf.getColumns()];
            chosenTiles = new Coordinates[maxNumOfChosenTiles];
            chosenOrder = new Integer[chosenTiles.length];
            orderNumbers = new JLabel[chosenTiles.length];
            isGameFinished = false;

            // set up the background
            JPanel background = new Background("visual_components/misc/sfondo parquet.jpg");
            background.setLayout(new BoxLayout(background,BoxLayout.PAGE_AXIS));
            JPanel upperPanel = new JPanel();
            upperPanel.setOpaque(false);
            upperPanel.setLayout(new FlowLayout());
            background.add(upperPanel);
            JPanel middlePanel = new JPanel();
            middlePanel.setOpaque(false);
            middlePanel.setLayout(new BoxLayout(middlePanel,BoxLayout.PAGE_AXIS));
            background.add(middlePanel);
            JPanel lowerPanel = new JPanel();
            lowerPanel.setOpaque(false);
            lowerPanel.setLayout(new FlowLayout());
            background.add(lowerPanel);
            JScrollPane scrollPane = new JScrollPane(background);
            scrollPane.setOpaque(false);
            scrollPane.setPreferredSize(new Dimension(1280,720));
            add(scrollPane);

            //creating the shelfPanels
            shelves = new ShelfPanel[gameView.getPlayers().length];
            JPanel shelvesPanel = new JPanel();
            shelvesPanel.setOpaque(false);
            shelvesPanel.setLayout(new BoxLayout(shelvesPanel,BoxLayout.PAGE_AXIS));
            upperPanel.add(shelvesPanel);
            scores = new JLabel[gameView.getPlayers().length];
            for(int i = 0; i<gameView.getPlayers().length;i++){
                PlayerView p = gameView.getPlayers()[i];
                if(p.getUsername().equals(username)){ // my infos
                    myId = i;
                    //set up the view of my shelf
                    shelves[i] = new ShelfPanel(p,"My shelf",500,500);
                    JPanel temp = new JPanel();
                    temp.setOpaque(false);
                    temp.setLayout(new BoxLayout(temp,BoxLayout.PAGE_AXIS));
                    columnChoicePanel = new JPanel();
                    columnChoicePanel.setOpaque(false);
                    columnChoicePanel.setLayout(new GridLayout(1, Shelf.getColumns(), (int)(shelves[i].width/25), (int)(shelves[i].height/62.5)));
                    columnChoicePanel.setBorder(BorderFactory.createEmptyBorder(0, (int)(shelves[i].width/8.33), 0, (int)(shelves[i].width/8.77)));
                    temp.add(columnChoicePanel);
                    temp.add(shelves[i]);
                    lowerPanel.add(temp);
                    //set up the view of my private goal
                    temp = new JPanel();
                    temp.setLayout(new BoxLayout(temp,BoxLayout.PAGE_AXIS));
                    temp.setOpaque(false);
                    JPanel temp1 = new JPanel();
                    temp1.setOpaque(false);
                    temp1.add(new PrivateGoalPanel(p.getPrivateGoal().getId(), 150, 225));
                    temp.add(temp1);
                    //set up the view of my score
                    temp1 = new JPanel();
                    temp1.setLayout(new FlowLayout());
                    temp1.setOpaque(false);
                    JLabel label = new JLabel("Current score: ");
                    label.setOpaque(false);
                    label.setFont(label.getFont().deriveFont(16f));
                    scores[i] = new JLabel(String.valueOf(gameView.getPlayers()[i].getScore()));
                    scores[i].setOpaque(false);
                    scores[i].setFont(scores[i].getFont().deriveFont(16f));
                    temp1.add(label);
                    temp1.add(scores[i]);
                    temp.add(temp1);
                    lowerPanel.add(temp);
                }
                else{
                    //set up the view of opponents username and score
                    JLabel name = new JLabel(gameView.getPlayers()[i].getUsername().length()>15 ? gameView.getPlayers()[i].getUsername().substring(0,15)+"..." : gameView.getPlayers()[i].getUsername());
                    name.setOpaque(false);
                    name.setFont(name.getFont().deriveFont(10f));
                    scores[i] = new JLabel(String.valueOf(gameView.getPlayers()[i].getScore()));
                    scores[i].setOpaque(false);
                    scores[i].setFont(scores[i].getFont().deriveFont(10f));
                    JPanel temp = new JPanel();
                    temp.setLayout(new FlowLayout(FlowLayout.CENTER));
                    temp.setOpaque(false);
                    temp.add(name);
                    temp.add(scores[i]);
                    shelvesPanel.add(temp);
                    //set up the view of opponents' shelves
                    shelves[i] = new ShelfPanel(p,p.getUsername()+"'s shelf",225,225);
                    shelvesPanel.add(shelves[i]);
                }
            }

            //creating the gameBoard Panel
            gameBoardPanel = new GameBoardPanel(gameView.getGameBoard(),700,700);
            upperPanel.add(gameBoardPanel);

            // creating the Panel containing global Goals
            JPanel goals = new JPanel();
            goals.setOpaque(false);
            goals.setLayout(new BoxLayout(goals, BoxLayout.PAGE_AXIS));
            upperPanel.add(goals);
            globalGoalPanel = new GlobalGoalPanel[gameView.getGlobalGoals().length];
            for(int i=0; i<gameView.getGlobalGoals().length;i++){
                globalGoalPanel[i] = new GlobalGoalPanel(gameView.getGlobalGoals()[i],225,150 );
                goals.add(globalGoalPanel[i]);
            }

            // creating the panel containing the error texts and the picked tiles
            text = new JLabel(){
                @Override
                public void setText(String s){
                    super.setText(s);
                    revalidate();
                    repaint();
                }
            };
            text.setOpaque(false);
            text.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            text.setFont(text.getFont().deriveFont(18f));
            middlePanel.add(text);
            if(myId == currentPlayer) text.setText("It is your turn, choose your tiles from the board");
            errorText = new JLabel(){
                @Override
                public void setText(String s){
                    super.setText(s);
                    revalidate();
                    repaint();
                }
            };
            errorText.setOpaque(false);
            errorText.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            errorText.setFont(errorText.getFont().deriveFont(16f));
            errorText.setForeground(Color.RED);
            middlePanel.add(errorText);
            pickedTilesPanel = new JPanel(){
                @Override
                public Component add(Component comp){
                    Component res = super.add(comp);
                    revalidate();
                    repaint();
                    return res;
                }
            };
            pickedTilesPanel.setOpaque(false);
            pickedTilesPanel.setLayout(new FlowLayout());
            middlePanel.add(pickedTilesPanel);

            pack();
            startWindow.dispose();
            startWindow = null;
            setVisible(true);
        }

        private void showColumnChoiceButtons(){
            columnChoicePanel.removeAll();
            JButton button;
            for(int i=0;i<Shelf.getColumns();i++){
                button = new JButton("\u2193"); //unicode code for arrow pointing down
                button.setOpaque(false);
                button.setBorderPainted(false);
                button.setContentAreaFilled(false);
                columnChoicePanel.add(button);
                int chosenColumn = i;
                button.addActionListener((e) -> {
                    errorText.setText("");
                    if(e.getSource() instanceof JButton && !isGameFinished){
                        //checks that is my turn
                        if(myId != currentPlayer){
                            errorText.setText("It is not your turn");
                            return;
                        }
                        for(int j=0;j<chosenOrder.length && chosenOrder[j]!=null;j++){
                            if(j+1 > freeSpacesInMyShelf[chosenColumn]){
                                errorText.setText("There is not enough space in the selected column");
                                return;
                            };
                        }
                        Coordinates[] finalChosenTiles = new Coordinates[chosenTiles.length];
                        for(int j=0;j<chosenOrder.length && chosenOrder[j]!=null;j++){
                            finalChosenTiles[j] = chosenTiles[chosenOrder[j]];
                            chosenTiles[chosenOrder[j]] = null;
                            orderNumbers[chosenOrder[j]] = null;
                            chosenOrder[j] = null;
                        }
                        finalChosenTiles = Arrays.stream(finalChosenTiles).filter(x -> x!=null).toArray(Coordinates[]::new);
                        columnChoicePanel.removeAll();
                        columnChoicePanel.revalidate();
                        columnChoicePanel.repaint();
                        pickedTilesPanel.removeAll();
                        pickedTilesPanel.revalidate();
                        pickedTilesPanel.repaint();
                        notifyListeners(new TurnActionMessage(username,finalChosenTiles,chosenColumn));
                    }
                });
            }
            columnChoicePanel.revalidate();
            columnChoicePanel.repaint();
        }

        private void update(GameView gw){
            if(gw.getGameBoard() != null) gameBoardPanel.update(gw.getGameBoard());
            if(gw.getPlayers() != null){
                for(int i=0;i<gw.getPlayers().length;i++) if(gw.getPlayers()[i] != null){
                    shelves[i].update(gw.getPlayers()[i].getShelf(), i == myId);
                    scores[i].setText(String.valueOf(gw.getPlayers()[i].getScore()));
                    if(gw.getPlayers()[i].isWinner()) {
                        if(myId == i) text.setText("YOU WON!");
                        else text.setText("YOU LOST!");
                        JButton close = new JButton("Return to Starting Menù");
                        close.setOpaque(false);
                        close.setBorderPainted(false);
                        close.setContentAreaFilled(false);
                        close.addActionListener((e) -> {
                            errorText.setText("");
                            if(e.getSource() instanceof JButton){
                                username = null;
                                gameWindow.dispose();
                                gameWindow = null;
                                startWindow = new StartWindow();
                                startWindow.askUsername();
                            }
                        });
                        pickedTilesPanel.add(close);

                        isGameFinished = true;
                    }
                }
            }
            if(gw.getCurrentPlayer() != null) {
                currentPlayer = gw.getCurrentPlayer();
                if(myId == currentPlayer) text.setText("It is your turn, choose your tiles from the board");
                else text.setText("Wait for your turn");
            }
            if(gw.getNumOfActivePlayers() != null) {
                if(gw.getNumOfActivePlayers() == 1) text.setText("Other players disconnected, wait for them to reconnect or wait to win by forfeit");
                if(gw.getNumOfActivePlayers() == 2 && numOfActivePlayers == 1) text.setText("a player reconnected, the game can go on");
                numOfActivePlayers = gw.getNumOfActivePlayers();
            }
            if(gw.getGlobalGoals() != null){
                for(int i=0;i<gw.getGlobalGoals().length;i++) if(gw.getGlobalGoals()[i] != null) globalGoalPanel[i].update(gw.getGlobalGoals()[i]);
            }
            if(gw.getCheater() != null){
                errorText.setText(gw.getCheater() + " tried to cheat");
                text.setText("It is your turn, choose your tiles from the board");
            }

            revalidate();
            repaint();
        }
    }



    @Override
    public void update(Message m) {
        ClientImplementation.logger.log(Level.INFO,"GUI Received message: " + m.getClass());

        if(m instanceof UsernameNotFoundMessage){
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
        else if(m instanceof UpdateViewMessage){
            if(gameWindow == null){
                gameWindow = new GameWindow(((UpdateViewMessage)m).getGameView());
            }
            else if(!gameWindow.isGameFinished) gameWindow.update(((UpdateViewMessage)m).getGameView());
        }
        else{
            ClientImplementation.logger.log(Level.INFO,"CLI: received message from server of type: " + m.getClass().getSimpleName() + " , but no notify has been implemented for this type of message");
        }
    }

    public void run(){
        startWindow = new StartWindow();
    }

}
