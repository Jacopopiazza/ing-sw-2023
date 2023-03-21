package it.polimi.ingsw.Exceptions;

public class InvalidColorException extends Exception{

    public InvalidColorException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidColorException() {

    }

}