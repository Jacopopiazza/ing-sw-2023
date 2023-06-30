package it.polimi.ingsw.Exceptions;

/**
 * The NullPlayersException is an exception that is thrown when an players is null during the game.
 * It is a subclass of the RuntimeException class.
 */

@SuppressWarnings("ALL")
public class NullPlayersException extends RuntimeException{

    /**
     * Constructs a new {@code NullPlayersException} with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */
    public NullPlayersException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new {@code NullPlayersException} with no error message.
     */
    public NullPlayersException() {

    }

}