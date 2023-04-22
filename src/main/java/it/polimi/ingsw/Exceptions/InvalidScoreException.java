package it.polimi.ingsw.Exceptions;

public class InvalidScoreException extends RuntimeException{

    public InvalidScoreException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidScoreException() {

    }

}