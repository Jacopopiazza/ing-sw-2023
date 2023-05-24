package it.polimi.ingsw.View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventListener;

public class GraphicalUI extends ClientManager{

    private class Frame extends JFrame{

        private JPanel content;
        private JLabel error;

        private Frame(){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(400, 400);
            setLayout(new BorderLayout());

            //add the background
            JPanel backgound = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Image image = new ImageIcon("visual_components/misc/sfondo parquet.jpg").getImage();
                    g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
                }
            };
            add(backgound);

            //add the content
            backgound.setLayout(new BorderLayout());
            content = new JPanel();
            content.setLayout(new BoxLayout(content,BoxLayout.PAGE_AXIS));
            content.add(new JLabel(new ImageIcon("visual_components/Publisher material/Title 2000x618px.jpg")));
            content.add(new JLabel("Do you want to use RMI or Socket?"));
            JPanel choices = new JPanel();
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

            backgound.add(content,BorderLayout.CENTER);
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

}
