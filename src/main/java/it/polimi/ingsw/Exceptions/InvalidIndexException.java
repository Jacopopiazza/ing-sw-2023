package it.polimi.ingsw.Exceptions;

/**
 * The InvalidIndexException is an exception that is thrown when an invalid index is encountered.
 * It is a subclass of the RuntimeException class.
 */

public class InvalidIndexException extends RuntimeException{

    /**
     * Constructs a new InvalidIndexException with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */

    public InvalidIndexException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new InvalidIndexException with no error message.
     */

    public InvalidIndexException() {

    }

}