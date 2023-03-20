package it.polimi.ingsw.Exceptions;

public class InvalidIndexException extends Exception{

    public InvalidIndexException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidIndexException() {

    }

}