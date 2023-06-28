package it.polimi.ingsw.Exceptions;

/**
 * The InvalidNumberOfPlayersException is an exception that is thrown when an invalid number of players is encountered.
 * It is a subclass of the RuntimeException class.
 */

public class InvalidNumberOfPlayersException extends RuntimeException{

    /**
     * Constructs a new {@code InvalidNumberOfPlayersException} with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */
    public InvalidNumberOfPlayersException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new {@code InvalidNumberOfPlayersException} with no error message.
     */
    public InvalidNumberOfPlayersException() {

    }

}