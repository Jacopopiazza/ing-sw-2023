package it.polimi.ingsw.Exceptions;

/**
 * The UsernameNotFoundException is an exception that is thrown when a username is not found.
 * It is a subclass of the Exception class.
 */

public class UsernameNotFoundException extends Exception{

    /**
     * Constructs a new UsernameNotFoundException with the specified error message.
     *
     * @param errorMessage the error message describing the exception
     */

    public UsernameNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a new UsernameNotFoundException with no error message.
     */

    public UsernameNotFoundException(){

    }

}
