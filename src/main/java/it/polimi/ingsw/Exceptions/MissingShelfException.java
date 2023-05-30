package it.polimi.ingsw.Exceptions;

/**
 * The MissingShelfException is an exception that is thrown when a required shelf is missing.
 * It is a subclass of the RuntimeException class.
 */

public class MissingShelfException extends RuntimeException{

    /**
     * Constructs a new MissingShelfException with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */

    public MissingShelfException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new MissingShelfException with no error message.
     */

    public MissingShelfException() {

    }

}