package it.polimi.ingsw.Exceptions;

/**
 * Custom exception class to handle invalid IP address errors.
 * Subclass of the Exception class.
 */
public class InvalidIPAddress extends Exception {

    /**
     * Constructs a new {@code InvalidIPAddress} instance with the specified detail message.
     *
     * @param message the detail message
     */
    public InvalidIPAddress(String message) {
        super(message);
    }
}
