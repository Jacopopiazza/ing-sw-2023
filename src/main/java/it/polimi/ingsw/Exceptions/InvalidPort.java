package it.polimi.ingsw.Exceptions;

/**
 * Custom exception class to handle invalid IP Port errors.
 * Subclass of Exception class.
 */
@SuppressWarnings("ALL")
public class InvalidPort extends Exception{

    /**
     * Constructs a new {@code InvalidPort} instance with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidPort(String message) {
        super(message);
    }
}
