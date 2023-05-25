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
    Game game = new Game(3);// Instanciated just for try
    public Board() {
        game.addPlayer("a", (message -> System.out.println("ciao")));
        game.addPlayer("b", (message -> System.out.println("ciao")));
        game.addPlayer("c", (message -> System.out.println("ciao")));
        //game.addPlayer("d", (message -> System.out.println("ciao")));
        game.init();
        try {
            game.refillGameBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            Image newimg = img.getScaledInstance( 68, 68,  java.awt.Image.SCALE_SMOOTH ) ;
            return new ImageIcon( newimg );
        }
    }

    private class Frame extends JFrame implements ActionListener{

        private JPanel content;
        private JLabel error;

        private List<Image> tileImages = new ArrayList<>();
        private Frame(){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(720, 720); // 1:1 proportion
            setLocationRelativeTo(null);    // in the middle of the screen

            JPanel background = new Background("visual_components/boards/livingroom.png");
            background.setToolTipText("GameBoard");
            int borderWidth = 30;
            background.setBorder(BorderFactory.createEmptyBorder(borderWidth, borderWidth, borderWidth, borderWidth + 5));
            setContentPane(background);

            GameBoardView gameBoardView = new GameBoardView(game.getGameBoard());
            Set<Coordinates> coordinatesSet = gameBoardView.getCoords();
            setLayout(new GridLayout(9, 9, 5, 6));

            List<ImageIcon> tileImages;
            ImageIcon icon;
            TileColor color;
            int index;

            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    JButton button = new JButton();
                    button.setLayout(new FlowLayout());
                    if(coordinatesSet.contains(new Coordinates(i, j))){
                        color = gameBoardView.getTile(new Coordinates(i, j)).getCOLOR();
                        index = gameBoardView.getTile(new Coordinates(i, j)).getID() % 3;
                        tileImages = ManageImage.getListOfTileImages(color);
                        icon = tileImages.get(index);
                        icon = ManageImage.resizeImageIcon(icon);
                        button.setIcon(icon);
                        button.addActionListener(this);
                    }else{
                        button.setOpaque(false);
                        button.setEnabled(false);
                        button.setBorderPainted(false);
                        button.setFocusPainted(false);
                        button.setContentAreaFilled(false);
                        button.addActionListener(e -> {
                            // Not to be managed
                        });
                    }
                    add(button);
                }
            }
            setVisible(true);
        }

        @Override
        public void actionPerformed(ActionEvent e) {

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
