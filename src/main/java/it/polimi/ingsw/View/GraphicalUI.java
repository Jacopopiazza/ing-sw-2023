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

        private JPanel request;
        private JLabel error;

        private Frame(){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1280, 720); // 16:9 proportion

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
            askConnection();
            content.add(request);

            //add the error
            error = new JLabel();
            error.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            error.setForeground(Color.decode("13806189"));
            content.add(error);

            background.add(content,BorderLayout.CENTER);
            add(background);

            setVisible(true);
        }

        private void askConnection(){
            request.removeAll();

            //set up the question
            JLabel text = new JLabel("Do you want to use RMI or Socket?");
            text.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            text.setFont(text.getFont().deriveFont(20f));
            text.setForeground(Color.decode("13806189"));
            request.add(text);

            //set up the options
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton rmi = new JButton("RMI");
            rmi.setPreferredSize(new Dimension(100,40));
            rmi.setBackground(Color.decode("13806189"));
            rmi.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(!connectionChosen(button)){
                        error.setText("Unable to connect with RMI");
                    }
                }
            });
            JButton socket = new JButton("Socket");
            socket.setPreferredSize(new Dimension(100,40));
            socket.setBackground(Color.decode("13806189"));
            socket.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(!connectionChosen(button)){
                        error.setText("Unable to connect with Socket");
                    }
                }
            });
            choices.add(rmi);
            choices.add(socket);
            request.add(choices);
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
