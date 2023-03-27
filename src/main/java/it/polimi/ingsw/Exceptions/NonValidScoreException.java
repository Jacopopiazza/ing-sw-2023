package it.polimi.ingsw.Exceptions;

public class NonValidScoreException extends RuntimeException{

    public NonValidScoreException(String errorMessage) {
        super(errorMessage);
    }

    public NonValidScoreException() {

    }

}