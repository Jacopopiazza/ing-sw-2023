package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.Game;
import it.polimi.ingsw.Model.GameBoard;
import it.polimi.ingsw.ModelView.GameBoardView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private class Frame extends JFrame implements ActionListener{

        private JPanel content;
        private JLabel error;

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
            String color;

            setLayout(new GridLayout(9, 9, 5, 6));
            for (int i = 0; i < 9; i++) {
                for (int j = 0; j < 9; j++) {
                    JButton button = new JButton();
                    if(coordinatesSet.contains(new Coordinates(i, j))){
                        color = gameBoardView.getTile(new Coordinates(i, j)).getCOLOR().toString();
                        button.addActionListener(this);
                        button.setText(color);
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
