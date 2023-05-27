package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.NoTileException;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.ModelView.GameBoardView;
import it.polimi.ingsw.ModelView.PrivateGoalView;
import it.polimi.ingsw.ModelView.ShelfView;
import it.polimi.ingsw.ModelView.TileView;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Board extends ClientManager {
    Game game = new Game(4);// Instanciated just for try
    GameBoardView gameBoardView;
    ShelfView shelfView;
    PrivateGoalView privateGoalView;
    public Board() {
        game.addPlayer("a", (message -> System.out.println("ciao")));
        game.addPlayer("b", (message -> System.out.println("ciao")));
        game.addPlayer("c", (message -> System.out.println("ciao")));
        game.addPlayer("d", (message -> System.out.println("ciao")));
        game.init();
        try {
            game.refillGameBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        gameBoardView = new GameBoardView(game.getGameBoard());
        Shelf shelf = new Shelf();
        try {
            shelf.addTile(new Tile(TileColor.BLUE,0),4);
            shelf.addTile(new Tile(TileColor.BLUE,1),1);
            shelf.addTile(new Tile(TileColor.BLUE,2),2);
            shelf.addTile(new Tile(TileColor.BLUE,3),3);
        } catch (NoTileException e) {
            throw new RuntimeException(e);
        } catch (IllegalColumnInsertionException e) {
            e.printStackTrace();
        }
        shelfView = new ShelfView(shelf);
        privateGoalView = new PrivateGoalView(game.getPlayer(0).getPrivateGoal());
    }

    public Board(GameBoardView gameBoardView, ShelfView shelfView, PrivateGoalView privateGoalView){
        this.gameBoardView = gameBoardView;
        this.shelfView = shelfView;
        this.privateGoalView = privateGoalView;
    }

    private class Background extends JPanel {
        private Image backgroundImage;

        private Background(String imagePath){
            super();
            backgroundImage = new ImageIcon(imagePath).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private class ImageManager {

        private static List<ImageIcon>[] tilesIcons = null;

        protected static List<ImageIcon> getListOfTileImages(TileColor color){
            if(tilesIcons == null){
                tilesIcons = new ArrayList[TileColor.values().length];
                for(TileColor tc : TileColor.values()){
                    tilesIcons[tc.ordinal()] = new ArrayList<>();
                    for(int i=0; i<3; i++){
                        tilesIcons[tc.ordinal()].add(new ImageIcon("visual_components/item tiles/"+tc.name().toLowerCase()+String.valueOf(i+1)+".png"));
                    }
                }
            }
            for(TileColor tc : TileColor.values()){
                if(tc.equals(color)) return tilesIcons[tc.ordinal()];
            }
            return null;
        }

        protected static ImageIcon resizeTileIcon(ImageIcon icon){
            Image img = icon.getImage();
            // dimension calculated from the board size (720x720)
            Image newimg = img.getScaledInstance( 68, 68,  java.awt.Image.SCALE_SMOOTH ) ;
            return new ImageIcon( newimg );
        }
    }

    private class GameBoardPanel {
        private Background gameBoardPanel;
        private List<ImageIcon> tileImages;
        private Set<Coordinates> coordinatesSet;
        private Dimension gameBoardDimension;
        int numberOfPicks;

        protected GameBoardPanel(){
            gameBoardDimension = new Dimension(720, 720);
            gameBoardPanel = new Background("visual_components/boards/livingroom.png");
            gameBoardPanel.setToolTipText("Game Board");
            gameBoardPanel.setLayout(new BorderLayout());
            gameBoardPanel.setPreferredSize(gameBoardDimension); // fixed dimension
            int borderWidth = 30;   // calculated from board dimension
            gameBoardPanel.setBorder(BorderFactory.createEmptyBorder(borderWidth, borderWidth + 1, borderWidth, borderWidth + 7));
            gameBoardPanel.setLayout(new GridLayout(9, 9, 5, 5));    // calculated from board dimension

            // getting the elements needed to create the button board
            coordinatesSet = gameBoardView.getCoords();
            numberOfPicks = 0;

            // setting the buttons in the board
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    JButton button = new JButton();
                    button.setLayout(new FlowLayout());
                    if(coordinatesSet.contains(new Coordinates(i, j))){
                        button = getTileButton(gameBoardView.getTile(new Coordinates(i, j)), i, j);
                    }else{
                        button = getVoidButton();
                    }
                    gameBoardPanel.add(button);
                }
            }
        }

        private JButton getTileButton(TileView tile, int x, int y){
            ImageIcon icon;
            TileColor color;
            int image_id;
            JButton button = new JButton();

            color = tile.getCOLOR();
            image_id = tile.getID() % 3;
            tileImages = ImageManager.getListOfTileImages(color);
            icon = tileImages.get(image_id);
            icon = ImageManager.resizeTileIcon(icon);
            button.setIcon(icon);
            button.addActionListener((e) -> {
                if(e.getSource() instanceof JButton pressed){
                    if(gameBoardView.isPickable(new Coordinates(x, y))){
                        // on every pick check that this tile is on the same line
                        if(numberOfPicks < 3) {
                            numberOfPicks++;
                            System.out.println("Pressed " + x + "-" + y);
                        }else{
                            System.out.println("Max number of tiles picked!");
                        }
                    }else{
                        System.out.println("Not Pickable!");
                    }
                }
            });
            return button;
        }

        private JButton getVoidButton(){
            JButton button = new JButton();
            button.setLayout(new FlowLayout());
            button.setOpaque(false);
            button.setEnabled(false);
            button.setBorderPainted(false);
            button.setFocusPainted(false);
            button.setContentAreaFilled(false);
            button.addActionListener(e -> {
                // Not to be managed
            });
            return button;
        }

        protected JPanel getGameBoardPanel(){
            return this.gameBoardPanel;
        }
    }


    private class ShelfPanel{
        private Background shelfPanel;
        private Dimension shelfDimension;

        private int rows;
        private int cols;

        protected ShelfPanel(ShelfView shelfView) {
            shelfDimension = new Dimension(500, 500);
            shelfPanel = new Background("visual_components/boards/bookshelf_orth.png");
            shelfPanel.setOpaque(false);
            shelfPanel.setToolTipText("My Shelf");
            shelfPanel.setLayout(new BorderLayout());
            shelfPanel.setPreferredSize(shelfDimension); // fixed dimension

            rows = Shelf.getRows();
            cols = Shelf.getColumns();
            shelfPanel.setLayout(new GridLayout(rows, cols, 20, 8));
            int borderWidth = 30;   // calculated from board dimension
            shelfPanel.setBorder(BorderFactory.createEmptyBorder(borderWidth + 2, borderWidth * 2,
                    borderWidth * 2 - 3, borderWidth * 2));


            // setting the void buttons in the board
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    if(shelfView.getTile(new Coordinates(i,j)) != null) shelfPanel.add(getTileLabel(shelfView.getTile(new Coordinates(i,j))));
                    else shelfPanel.add(getVoidLabel());
                }
            }


        }

        private JLabel getTileLabel(TileView tile){
            int image_id = tile.getID()%3;
            ImageIcon ic = ImageManager.getListOfTileImages(tile.getCOLOR()).get(image_id);
            return new JLabel(ImageManager.resizeTileIcon(ic));
        }

        private JLabel getVoidLabel(){
            JLabel label = new JLabel();
            label.setOpaque(false);
            return label;
        }

        protected JPanel getShelfPanel(){
            return this.shelfPanel;
        }
    }


    private class PrivateGoalPanel{
        private JPanel privateGoalPanel;
    }


    private class GameWindow extends JFrame{
        GameBoardPanel gameBoardPanel;
        ShelfPanel shelfPanel;
        PrivateGoalPanel privateGoalPanel;
        private GameWindow(){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1280, 720); // if not set the window appears in the right bottom corner
            setLocationRelativeTo(null);    // in the middle of the screen

            // this background can be resized
            JPanel background = new Background("visual_components/misc/sfondo parquet.jpg");
            setContentPane(background);

            //creating the gameBoard Panel
            gameBoardPanel = new GameBoardPanel();
            JPanel gameBoard = gameBoardPanel.getGameBoardPanel();
            // setting it at the center
            add(gameBoard, BorderLayout.CENTER);

            //creating the shelfPanel Panel
            shelfPanel = new ShelfPanel(shelfView);
            JPanel shelf = shelfPanel.getShelfPanel();
            //setting it to the center-right
            add(shelf, BorderLayout.EAST);

            //creating the privateGoalPanel Panel
            //setting it to the center-left

            pack();
            setVisible(true);
        }
    }

    @Override
    public void update(Message m) {

    }

    @Override
    public void run() {
        GameWindow gameWindow = new GameWindow();
    }
}
