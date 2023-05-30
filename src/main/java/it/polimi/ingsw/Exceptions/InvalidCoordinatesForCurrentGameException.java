package it.polimi.ingsw.Exceptions;

/**
 * The InvalidCoordinatesForCurrentGameException is an exception that is thrown when invalid coordinates are provided for the current game state.
 * It is a subclass of the RuntimeException class.
 */

public class InvalidCoordinatesForCurrentGameException extends RuntimeException{

    /**
     * Constructs a new InvalidCoordinatesForCurrentGameException with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */

    public InvalidCoordinatesForCurrentGameException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new InvalidCoordinatesForCurrentGameException with no error message.
     */

    public InvalidCoordinatesForCurrentGameException() {

    }

}