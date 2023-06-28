package it.polimi.ingsw.Messages;

import it.polimi.ingsw.ModelView.GameView;

/**
 * The UpdateViewMessage class represents a message containing an updated game view.
 * It extends the {@code Message} abstract class and is serializable.
 */
public class UpdateViewMessage extends Message {
    private final GameView gameView;

    /**
     * Constructs a new {@code UpdateViewMessage} object with the specified game view.
     *
     * @param gv The updated game view.
     */

    public UpdateViewMessage(GameView gv) {
        this.gameView = gv;
    }

    /**
     * Returns the game view.
     *
     * @return The game view.
     */
    public GameView getGameView(){return gameView;}
}
