package it.polimi.ingsw.Messages;

import it.polimi.ingsw.ModelView.GameView;

import java.io.Serializable;

public class UpdateViewMessage implements Message, Serializable {
    private GameView gameView;

    public UpdateViewMessage(GameView gv) {
        this.gameView = gv;
    }
}
