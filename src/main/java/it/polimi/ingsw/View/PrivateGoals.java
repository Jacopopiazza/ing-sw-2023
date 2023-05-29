package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Exceptions.IllegalColumnInsertionException;
import it.polimi.ingsw.Exceptions.NoTileException;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.ModelView.*;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PrivateGoals extends ClientManager{

    Game game = new Game(4);// Instanciated just for try
    GameBoardView gameBoardView;
    ShelfView shelfView;
    PrivateGoalView privateGoalView;

    GlobalGoalView globalGoalView;
    public PrivateGoals() {
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
        try {
            globalGoalView = new GlobalGoalView(game.getGoals()[0]);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
    }

    public PrivateGoals(GameBoardView gameBoardView, ShelfView shelfView, PrivateGoalView privateGoalView){
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

        private static ImageIcon resizeIcon(ImageIcon icon, int width, int height){
            Image img = icon.getImage();
            // dimension calculated from the board size (720x720)
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
            return new JLabel(ImageManager.resizeIcon(ic,width,height));
        }

    }

    private class GameBoardPanel extends Background{
        private int width;
        private int height;
        private static final int gameBoardDim = 9;
        int numberOfPicks;

        private GameBoardPanel(GameBoardView gameBoard,int width, int height){
            super("visual_components/boards/livingroom.png");
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
            JButton button = new JButton(ImageManager.resizeIcon(icon,(int)(width/10.59),(int)(height/10.59)));
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


    private class PrivateGoalPanel extends Background{
        private PrivateGoalPanel(String imagePath, String toolTip, int width, int height, int pvtGoalIndex){
            super(imagePath + "Personal_Goals" + pvtGoalIndex + ".png");
            setOpaque(false);
            setToolTipText(toolTip);
            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(width,height));
            setLayout(new FlowLayout());
        }

    }


    private class PublicGoalPanel extends Background{
        private int displayedScore;
        private ImageIcon scoreIcon;
        private JLabel score;
        private int width;
        private int height;
        private PublicGoalPanel(GlobalGoalView globalGoalView1, String imagePath, String toolTip, int width, int height, int commonGoalCard){
            super(imagePath + commonGoalCard + ".jpg");
            this.width = width;
            this.height = height;
            setPreferredSize(new Dimension(this.width, this.height));
            setLayout(new BorderLayout());
            setOpaque(false);
            setToolTipText(toolTip);
            setLayout(new BorderLayout());
            setLayout(new FlowLayout());

            // add the scorecard
            score = new JLabel();
            this.displayedScore = globalGoalView.getCurrentScore();

            if(displayedScore > 0){
                scoreIcon = new ImageIcon("visual_components/scoring tokens/scoring_" + displayedScore + ".jpg");
            }else{
                scoreIcon = new ImageIcon("visual_components/scoring tokens/scoring_EMPTY.jpg");
            }

            // rotate
            scoreIcon = ImageManager.rotateImageIcon(scoreIcon, -7);

            score.setToolTipText("Score");
            add(score);
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            scoreIcon = ImageManager.resizeIcon(scoreIcon, (int) (width/3.675), (int) (height/2.45));
            g.drawImage(scoreIcon.getImage(), (int)(width/1.65), (int)(height/3.8), this);
        }

    }

    private class GameWindow extends JFrame{
        GameBoardPanel gameBoardPanel;
        ShelfPanel shelfPanel;
        PrivateGoalPanel privateGoalPanel;
        PublicGoalPanel publicGoalPanel;
        private GameWindow(){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1280, 720); // if not set the window appears in the right bottom corner
            setLocationRelativeTo(null);    // in the middle of the screen

            //creating the privateGoalPanel Panel
            privateGoalPanel = new PrivateGoalPanel("visual_components/personal goal cards/", "My Private Goals", 200, 300, 1);

            //creating the publicGoalsPanel Panel
            publicGoalPanel = new PublicGoalPanel(null, "visual_components/common goal cards/", "Common Goal", 300, 200, 3);

            //creating the gameBoard Panel
            gameBoardPanel = new GameBoardPanel(gameBoardView,720,720);

            //creating the shelfPanel Panel
            shelfPanel = new ShelfPanel(shelfView,"visual_components/boards/bookshelf_orth.png","My Shelf",500,600);

            // this background can be resized
            Background background = new Background("visual_components/misc/sfondo parquet.jpg");

            background.add(privateGoalPanel, BorderLayout.WEST);
            background.add(publicGoalPanel, BorderLayout.WEST);
            background.add(gameBoardPanel, BorderLayout.CENTER);
            background.add(shelfPanel, BorderLayout.EAST);

            JScrollPane scrollPane = new JScrollPane(background);
            setContentPane(scrollPane);
            //pack();
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

