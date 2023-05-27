package it.polimi.ingsw.View;

import it.polimi.ingsw.Client.AppClientImplementation;
import it.polimi.ingsw.Client.ClientManager;
import it.polimi.ingsw.Messages.*;
import it.polimi.ingsw.Model.Utilities.Config;

import javax.swing.*;
import java.awt.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.List;

public class GraphicalUI extends ClientManager {

    String username;

    StartWindow startWindow;

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

    private class StartWindow extends JFrame{
        private JPanel request;
        private JLabel error;

        private StartWindow(){
            super("My Shelfie");
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(1120, 630); // 16:9 proportion
            setLocationRelativeTo(null);

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
            error = new JLabel();
            askConnection();
            content.add(request);

            //add the error
            error.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            error.setForeground(Color.decode("14929049"));
            content.add(error);

            background.add(content,BorderLayout.CENTER);
            add(background);

            setVisible(true);
        }

        private void askConnection(){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("Do you want to use RMI or Socket?"));

            //set up the options
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton rmi = getStandardButton("RMI");
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
            JButton socket = getStandardButton("Socket");
            socket.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(connectionChosen(button)){
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
            request.revalidate();
            request.repaint();
        }

        private void askUsername(){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("Insert the username"));

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
            JButton submit = getStandardButton("Submit");
            submit.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    username = inputText.getText();
                    inputText.setText("");
                    if (username.contains(" ")){
                        error.setText("Spaces are not allowed in the username");
                        username = null;
                    }
                    else doReconnect(username);
                }
            });

            panel.add(inputText);
            panel.add(submit);
            request.add(panel);
            request.revalidate();
            request.repaint();
        }

        private void askLobby(){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("Do you want to create a lobby or to join a lobby?"));

            //set up the options
            JPanel choices = new JPanel();
            choices.setOpaque(false);
            choices.setLayout(new FlowLayout());
            JButton create = getStandardButton("Create");
            create.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(button.getText().equals("Create")) {
                        askNumOfPlayersInLobby();
                    }
                }
            });
            JButton join = getStandardButton("Join");
            join.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(button.getText().equals("Join")) {
                        doConnect(username,1);
                    }
                }
            });
            choices.add(create);
            choices.add(join);
            request.add(choices);
            request.revalidate();
            request.repaint();
        }

        private void askNumOfPlayersInLobby(){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("How many players do you want in your lobby?"));

            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setLayout(new FlowLayout());

            //set up the options
            for(int i = 2; i <= Config.getInstance().getMaxNumberOfPlayers();i++){
                JButton choice = getStandardButton(String.valueOf(i));
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
            request.revalidate();
            request.repaint();
        }

        private void showLobby(List<String> players){
            request.removeAll();
            error.setText("");

            //set up the question
            request.add(getStandardText("players in lobby:"));
            for(String p : players) request.add(getStandardText(p));

            //set up the exit button
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            JButton exit = getStandardButton("Exit");
            exit.addActionListener((e) -> {
                if(e.getSource() instanceof JButton button){
                    if(button.getText().equals("Exit")) {
                        doQuit(username);
                        username = null;
                        askUsername();
                    }
                }
            });

            panel.add(exit);
            request.add(panel);
            request.revalidate();
            request.repaint();
        }

        private JButton getStandardButton(String name){
            JButton button = new JButton(name);
            button.setPreferredSize(new Dimension(100,40));
            button.setBackground(Color.decode("14929049"));
            button.setForeground(Color.decode("5776384"));
            button.setFont(button.getFont().deriveFont(18f));
            return button;
        }

        private JLabel getStandardText(String text){
            JLabel label = new JLabel(text);
            label.setAlignmentX(JLabel.CENTER_ALIGNMENT);
            label.setFont(label.getFont().deriveFont(20f));
            label.setForeground(Color.decode("14929049"));
            return label;
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
        AppClientImplementation.logger.log(Level.INFO,"GUI Received message");

        if(m instanceof NoUsernameToReconnectMessage){
            startWindow.askLobby();
        }
        else if(m instanceof TakenUsernameMessage){
            username = null;
            startWindow.askUsername();
            startWindow.error.setText("Username is already taken");
        }
        else if(m instanceof NoLobbyAvailableMessage){
            startWindow.error.setText("There are no lobbies available at the moment, create a new one");
        }
        else if(m instanceof LobbyMessage){
            startWindow.showLobby(((LobbyMessage) m).getPlayers());
        }
        else if(m instanceof GameServerMessage){
            cleanListeners();
            addListener((message) -> {
                try{
                    ((GameServerMessage) m).getServer().handleMessage(message,client);
                }catch (RemoteException e){
                    e.printStackTrace();
                }
            });
        }
        else{
            AppClientImplementation.logger.log(Level.INFO,"CLI: received message from server of type: " + m.getClass().getSimpleName() + " , but no notify has been implemented for this type of message");
        }
    }

    public void run(){
        startWindow = new StartWindow();
    }

}
