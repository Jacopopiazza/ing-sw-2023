package it.polimi.ingsw.Exceptions;

/**
 * The EmptySackException is an exception that is thrown when trying to access an empty tile sack.
 * It is a subclass of the Exception class.
 */
public class EmptySackException extends Exception {

    /**
     * Constructs a new EmptySackException with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */
    public EmptySackException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new EmptySackException with no error message.
     */
    public EmptySackException(){

    }

}
