package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.TileColor;
import it.polimi.ingsw.ModelView.GameBoardView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Board extends ClientManager {
    Game game = new Game(4);// Instanciated just for try
    GameBoardView gameBoardView;
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
    }

    public Board(GameBoardView gameBoardView){
        this.gameBoardView = gameBoardView;
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

    private class ManageImage{
        protected static List<ImageIcon> getListOfTileImages(TileColor color){
            List<ImageIcon> tileImagesWhite = new ArrayList<>();
            List<ImageIcon> tileImagesFuchsia = new ArrayList<>();
            List<ImageIcon> tileImagesBlue = new ArrayList<>();
            List<ImageIcon> tileImagesCyan = new ArrayList<>();
            List<ImageIcon> tileImagesYellow = new ArrayList<>();
            List<ImageIcon> tileImagesGreen = new ArrayList<>();

            tileImagesBlue.add(new ImageIcon("visual_components/item tiles/Cornici1.1.png"));
            tileImagesBlue.add(new ImageIcon("visual_components/item tiles/Cornici1.2.png"));
            tileImagesBlue.add(new ImageIcon("visual_components/item tiles/Cornici1.3.png"));

            tileImagesGreen.add(new ImageIcon("visual_components/item tiles/Gatti1.1.png"));
            tileImagesGreen.add(new ImageIcon("visual_components/item tiles/Gatti1.2.png"));
            tileImagesGreen.add(new ImageIcon("visual_components/item tiles/Gatti1.3.png"));

            tileImagesYellow.add(new ImageIcon("visual_components/item tiles/Giochi1.1.png"));
            tileImagesYellow.add(new ImageIcon("visual_components/item tiles/Giochi1.2.png"));
            tileImagesYellow.add(new ImageIcon("visual_components/item tiles/Giochi1.3.png"));

            tileImagesWhite.add(new ImageIcon("visual_components/item tiles/Libri1.1.png"));
            tileImagesWhite.add(new ImageIcon("visual_components/item tiles/Libri1.2.png"));
            tileImagesWhite.add(new ImageIcon("visual_components/item tiles/Libri1.3.png"));

            tileImagesFuchsia.add(new ImageIcon("visual_components/item tiles/Piante1.1.png"));
            tileImagesFuchsia.add(new ImageIcon("visual_components/item tiles/Piante1.2.png"));
            tileImagesFuchsia.add(new ImageIcon("visual_components/item tiles/Piante1.3.png"));

            tileImagesCyan.add(new ImageIcon("visual_components/item tiles/Trofei1.1.png"));
            tileImagesCyan.add(new ImageIcon("visual_components/item tiles/Trofei1.2.png"));
            tileImagesCyan.add(new ImageIcon("visual_components/item tiles/Trofei1.3.png"));

            switch (color){
                case YELLOW -> { return tileImagesYellow; }
                case CYAN -> { return tileImagesCyan; }
                case GREEN -> { return tileImagesGreen; }
                case BLUE -> { return tileImagesBlue; }
                case WHITE -> { return tileImagesWhite; }
                case FUCHSIA -> { return tileImagesFuchsia; }
            }
            return null;
        }

        protected static ImageIcon resizeImageIcon(ImageIcon icon){
            Image img = icon.getImage();
            // dimension calculated from the board size (720x720)
            Image newimg = img.getScaledInstance( 68, 68,  java.awt.Image.SCALE_SMOOTH ) ;
            return new ImageIcon( newimg );
        }
    }

    private class GameBoardGUI implements ActionListener{
        private Background board;
        private List<ImageIcon> tileImages;
        private Set<Coordinates> coordinatesSet;
        private ImageIcon icon;
        private TileColor color;
        private int image_id;

        private Dimension boardDimension;

        protected GameBoardGUI(){
            boardDimension = new Dimension(720, 720);
            board = new Background("visual_components/boards/livingroom.png");
            board.setLayout(new BorderLayout());
            board.setPreferredSize(boardDimension); // fixed dimension
            int borderWidth = 30;   // calculated from board dimension
            board.setBorder(BorderFactory.createEmptyBorder(borderWidth, borderWidth + 1, borderWidth, borderWidth + 7));
            board.setLayout(new GridLayout(9, 9, 5, 5));    // calculated from board dimension

            // getting the elements needed to create the button board
            coordinatesSet = gameBoardView.getCoords();

            // setting the buttons in the board
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    JButton button = new JButton();
                    button.setLayout(new FlowLayout());
                    if(coordinatesSet.contains(new Coordinates(i, j))){
                        color = gameBoardView.getTile(new Coordinates(i, j)).getCOLOR();
                        image_id = gameBoardView.getTile(new Coordinates(i, j)).getID() % 3;
                        tileImages = ManageImage.getListOfTileImages(color);
                        icon = tileImages.get(image_id);
                        icon = ManageImage.resizeImageIcon(icon);
                        button.setIcon(icon);
                        button.addActionListener(this); // Maybe is better the button
                    }else{
                        button = getVoidButton();
                    }
                    board.add(button);
                }
            }
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

        protected JPanel getGameBoardGUI(){
            return this.board;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // To be implemented
        }
    }


    private class Frame extends JFrame{
        GameBoardGUI gameBoardGUI;
        private Frame(){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(900, 900); // if not set the window appears in the right bottom corner
            setLocationRelativeTo(null);    // in the middle of the screen

            // this background can be resized
            JPanel background = new Background("visual_components/misc/sfondo parquet.jpg");
            background.setToolTipText("GameBoard");
            setContentPane(background);

            //creating the gameBoard Panel
            gameBoardGUI = new GameBoardGUI();
            JPanel board = gameBoardGUI.getGameBoardGUI();

            // setting it at the center
            add(board, BorderLayout.CENTER);
            pack();
            setVisible(true);
        }
    }

    @Override
    public void update(Message m) {

    }

    @Override
    public void run() {
        Frame frame = new Frame();
    }
}
