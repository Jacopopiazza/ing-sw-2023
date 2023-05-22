package it.polimi.ingsw.TUI;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;


import java.io.IOException;


public class UI {
    // Write this title in a config file
    String r1 = " __    __           ______ _           _  __  _";
    String r2 = "|  \\  /  |         /  ____| |         | |/ _|(_)";
    String r3 = "| \\ \\/ / |_   _   |  (___ | |__   ___ | | |_  _  ___";
    String r4 = "| |\\__/| | | | |   \\___  \\|  _ \\ / _ \\| |  _|| |/ _ \\";
    String r5 = "| |    | | |_| |    ___)  | | | |  __/| | |  | |  __/";
    String r6 = "|_|    |_|\\__, |   |_____/|_| |_|\\___/|_|_|  |_|\\___/";
    String r7 = "            _/ |";
    String r8 = "           |__/";

    private final Terminal terminal;
    DefaultTerminalFactory defaultTerminalFactory;
    private final TextGraphics textGraphics;
    int padding;

    String user = null;

    // Needs the view

    public UI() {
        defaultTerminalFactory = new DefaultTerminalFactory();
        padding = 15;
        try {
            terminal = defaultTerminalFactory.createTerminal();
            terminal.enterPrivateMode();
            terminal.clearScreen();
            terminal.setCursorVisible(false);
            terminal.enableSGR(SGR.BOLD);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            textGraphics = terminal.newTextGraphics();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void showTitle(){
        try {
            textGraphics.setForegroundColor(TextColor.ANSI.WHITE);
            textGraphics.setBackgroundColor(TextColor.ANSI.BLACK);

            textGraphics.putString(padding, 1, r1);
            textGraphics.putString(padding, 2, r2);
            textGraphics.putString(padding, 3, r3);
            textGraphics.putString(padding, 4, r4);
            textGraphics.putString(padding, 5, r5);
            textGraphics.putString(padding, 6, r6);
            textGraphics.putString(padding, 7, r7);
            textGraphics.putString(padding, 8, r8);

            terminal.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private String readUsername(){
        try {
            textGraphics.putString(new TerminalPosition(padding * 2, 10), "Insert username: ");
            terminal.setCursorVisible(true);
            terminal.flush();

            StringBuilder input = new StringBuilder();
            int x = 1 + padding * 2 + "Insert username: ".length(); // Start reading input after the prompt

            while (true) {
                KeyStroke keyStroke = terminal.pollInput();

                if (keyStroke != null) {
                    if (keyStroke.getKeyType() == KeyType.Enter) {
                        break; // Exit the loop when Enter key is pressed
                    } else if (keyStroke.getKeyType() == KeyType.Character) {
                        char c = keyStroke.getCharacter();
                        input.append(c);
                        textGraphics.putString(new TerminalPosition(x, 10),
                                Character.toString(c));
                        x++;
                        terminal.flush();
                    }
                }
            }
            return input.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public void DAJEROMADAJE(){
        showTitle();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        user = readUsername();
    }

}
