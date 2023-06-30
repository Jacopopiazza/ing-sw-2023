package it.polimi.ingsw.Exceptions;

/**
 * The IllegalColumnInsertionException is an exception that is thrown when trying to insert a tile into an illegal column on a game board.
 * It is a subclass of the Exception class.
 */

@SuppressWarnings("ALL")
public class IllegalColumnInsertionException extends Exception{

    /**
     * Constructs a new {@code IllegalColumnInsertionException} with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */

    public IllegalColumnInsertionException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new {@code IllegalColumnInsertionException} with no error message.
     */

    public IllegalColumnInsertionException() {

    }

}