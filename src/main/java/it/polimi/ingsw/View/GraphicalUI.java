package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Messages.Message;
import it.polimi.ingsw.Model.Utilities.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.EventListener;

public class GraphicalUI extends ClientManager {

    String username;

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
            error.setForeground(Color.decode("14929049"));
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
            text.setForeground(Color.decode("14929049"));
            request.add(text);

            //set up the options
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton rmi = new JButton("RMI");
            rmi.setPreferredSize(new Dimension(100,40));
            rmi.setBackground(Color.decode("14929049"));
            rmi.setForeground(Color.decode("5776384"));
            rmi.setFont(rmi.getFont().deriveFont(18f));
            rmi.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(connectionChosen(button)) {
                        askUsername();
                    }
                    else{
                        error.setText("Unable to connect with RMI");
                    }
                }
            });
            JButton socket = new JButton("Socket");
            socket.setPreferredSize(new Dimension(100,40));
            socket.setBackground(Color.decode("14929049"));
            socket.setForeground(Color.decode("5776384"));
            socket.setFont(socket.getFont().deriveFont(18f));
            socket.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(!connectionChosen(button)){
                        askUsername();
                    }
                    else{
                        error.setText("Unable to connect with Socket");
                    }
                }
            });
            choices.add(rmi);
            choices.add(socket);
            request.add(choices);
        }

        private void askUsername(){
            request.removeAll();

            //set up the question
            JLabel text = new JLabel("Insert the username");
            text.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            text.setFont(text.getFont().deriveFont(20f));
            text.setForeground(Color.decode("14929049"));
            request.add(text);

            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new FlowLayout());

            //set up the username text box
            JTextField inputText = new JTextField();
            inputText.setPreferredSize(new Dimension(450,40));
            inputText.setFont(inputText.getFont().deriveFont(20f));
            inputText.setBackground(Color.decode("14929049"));
            inputText.setForeground(Color.decode("5776384"));

            //set up the submit button
            JButton submit = new JButton("Submit");
            submit.setPreferredSize(new Dimension(100,40));
            submit.setBackground(Color.decode("14929049"));
            submit.setForeground(Color.decode("5776384"));
            submit.setFont(submit.getFont().deriveFont(18f));
            submit.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    doReconnect(inputText.getText());
                    inputText.setText("");
                }
            });

            panel.add(inputText);
            panel.add(submit);
            request.add(panel);
        }

        private void askLobby(){
            request.removeAll();

            //set up the question
            JLabel text = new JLabel("Do you want to create a lobby or to join a lobby?");
            text.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            text.setFont(text.getFont().deriveFont(20f));
            text.setForeground(Color.decode("14929049"));
            request.add(text);

            //set up the options
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton create = new JButton("Create");
            create.setPreferredSize(new Dimension(100,40));
            create.setBackground(Color.decode("14929049"));
            create.setForeground(Color.decode("5776384"));
            create.setFont(create.getFont().deriveFont(18f));
            create.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(button.getText().equals("Create")) {
                        doConnect(username,1);
                    }
                }
            });
            JButton join = new JButton("Join");
            join.setPreferredSize(new Dimension(100,40));
            join.setBackground(Color.decode("14929049"));
            join.setForeground(Color.decode("5776384"));
            join.setFont(join.getFont().deriveFont(18f));
            join.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(button.getText().equals("Join")) {
                        askNumOfPlayersInLobby();
                    }
                }
            });
            choices.add(create);
            choices.add(join);
            request.add(choices);
        }

        private void askNumOfPlayersInLobby(){
            request.removeAll();

            //set up the question
            JLabel text = new JLabel("How many players do you want in your lobby?");
            text.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            text.setFont(text.getFont().deriveFont(20f));
            text.setForeground(Color.decode("14929049"));
            request.add(text);

            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new FlowLayout());

            //set up the options
            for(int i = 2; i <= Config.getInstance().getMaxNumberOfPlayers();i++){
                JButton choice = new JButton(String.valueOf(i));
                choice.setPreferredSize(new Dimension(100,40));
                choice.setBackground(Color.decode("14929049"));
                choice.setForeground(Color.decode("5776384"));
                choice.setFont(choice.getFont().deriveFont(18f));
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



    @Override
    public void update(Message m) {

    }

    public void run(){
        Frame frame = new Frame();
    }

}
