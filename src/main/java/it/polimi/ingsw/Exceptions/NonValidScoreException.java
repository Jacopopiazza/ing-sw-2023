package it.polimi.ingsw.Exceptions;

public class NonValidScoreException extends Exception{

    public NonValidScoreException(String errorMessage) {
        super(errorMessage);
    }

    public NonValidScoreException() {

    }

}