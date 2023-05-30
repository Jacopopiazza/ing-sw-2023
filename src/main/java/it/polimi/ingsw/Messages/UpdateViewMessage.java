package it.polimi.ingsw.Messages;

import it.polimi.ingsw.ModelView.GameView;

import java.io.Serializable;

/**
 * The UpdateViewMessage class represents a message containing an updated game view.
 * It implements the Message and Serializable interfaces.
 */
public class UpdateViewMessage implements Message, Serializable {
    private GameView gameView;

    /**
     * Constructs an UpdateViewMessage object with the specified game view.
     *
     * @param gv The updated game view.
     */

    public UpdateViewMessage(GameView gv) {
        this.gameView = gv;
    }

    /**
     * Returns the updated game view.
     *
     * @return The game view.
     */
    public GameView getGameView(){return gameView;}
}
