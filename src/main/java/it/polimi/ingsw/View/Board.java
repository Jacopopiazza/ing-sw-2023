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
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
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

        private static JLabel getTileLabel(TileView tile,int width,int height){
            ImageIcon ic = ImageManager.getTileImage(tile.getCOLOR(),tile.getID()%3,false);
            return new JLabel(ImageManager.resizeImageIcon(ic,width,height));
        }

    }

    private class GameBoardPanel extends Background{
        private final static ImageIcon victoryToken = new ImageIcon("visual_components/scoring tokens/end game.jpg");
        private final static ImageIcon boardIcon = new ImageIcon("visual_components/boards/livingroom.png");
        private int width;
        private int height;
        private static final int gameBoardDim = 9;
        private boolean isFinished;
        private int numberOfPicks;

        private GameBoardPanel(GameBoardView gameBoard,int width, int height,boolean isFinished){
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

    private class ShelfPanel extends Background{
        private static final int rows = Shelf.getRows();
        private static final int cols = Shelf.getColumns();

        private ShelfPanel(ShelfView shelfView,String imagePath,String toolTip,int width, int height){
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
            GameBoardPanel gameBoard = new GameBoardPanel(gameBoardView,720,720,false);
            // setting it at the center
            add(gameBoard, BorderLayout.CENTER);

            //creating the shelfPanel Panel
            ShelfPanel shelf = new ShelfPanel(shelfView,"visual_components/boards/bookshelf_orth.png","My Shelf",500,600);
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
