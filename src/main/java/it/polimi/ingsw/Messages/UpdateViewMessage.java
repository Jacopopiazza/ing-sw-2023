package it.polimi.ingsw.Messages;

import it.polimi.ingsw.ModelView.GameView;

public class UpdateViewMessage implements Message {
    private GameView gameView;

    public UpdateViewMessage(GameView gv){
        this.gameView = gv;
    }
}
