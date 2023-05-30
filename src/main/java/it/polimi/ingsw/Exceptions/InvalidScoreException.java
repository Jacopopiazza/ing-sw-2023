package it.polimi.ingsw.Exceptions;

/**
 * The InvalidScoreException is an exception that is thrown when an invalid score is encountered.
 * It is a subclass of the RuntimeException class.
 */

public class InvalidScoreException extends RuntimeException{

    /**
     * Constructs a new InvalidScoreException with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */

    public InvalidScoreException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new InvalidScoreException with no error message.
     */

    public InvalidScoreException() {

    }

}