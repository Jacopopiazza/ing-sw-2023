package it.polimi.ingsw.Exceptions;

public class InvalidIndexException extends RuntimeException{

    public InvalidIndexException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidIndexException() {

    }

}