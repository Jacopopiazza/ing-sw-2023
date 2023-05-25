package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Messages.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.EventListener;

public class GraphicalUI extends ClientManager {

    private class Background extends JPanel{
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

    private class Frame extends JFrame{

        private JPanel content;
        private JLabel error;

        private Frame(){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1280, 720); // 16:9 proportion

            //add the background
            JPanel background = new Background("visual_components/misc/sfondo parquet.jpg");
            background.setLayout(new BorderLayout());
            add(background);

            //add the content
            content = new JPanel();
            content.setOpaque(false);
            content.setLayout(new BoxLayout(content,BoxLayout.PAGE_AXIS));
            Image titleImage = new ImageIcon("visual_components/Publisher material/Title 2000x618px.png").getImage();
            ImageIcon titleIcon = new ImageIcon(titleImage.getScaledInstance(1000,309,Image.SCALE_DEFAULT));
            content.add(new JLabel(titleIcon));
            content.add(new JLabel("Do you want to use RMI or Socket?"));
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton rmi = new JButton("RMI");
            rmi.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(!connectionChosen(button)){
                        error.setText("Unable to connect");
                    }
                }
            });
            JButton socket = new JButton("Socket");
            socket.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(!connectionChosen(button)){
                        error.setText("Unable to connect");
                    }
                }
            });
            choices.add(rmi);
            choices.add(socket);
            content.add(choices);
            error = new JLabel();
            content.add(error);
            content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            background.add(content,BorderLayout.CENTER);

            setVisible(true);
        }

    }

    private boolean connectionChosen (JButton button) {
        if(button.getText().equals("RMI")){
            try{
                this.setUpRMIClient();
                return true;
            }catch (RemoteException | NotBoundException ex ){
                return false;
            }
        } else if (button.getText().equals("Socket")) {
            try{
                this.setUpSocketClient();
                return true;
            }catch (RemoteException | NotBoundException ex ){
                return false;
            }
        }
        return false;
    }

    @Override
    public void update(Message m) {

    }

    public void run(){
        Frame frame = new Frame();
    }

}
