package it.polimi.ingsw.Exceptions;

/**
 * The NoTileException is an exception that is thrown when a tile is not found.
 * It is a subclass of the Exception class.
 */

public class NoTileException extends Exception{

    /**
     * Constructs a new {@code NoTileException} with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */

    public NoTileException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new {@code NoTileException} with no error message.
     */

    public NoTileException() {

    }

}