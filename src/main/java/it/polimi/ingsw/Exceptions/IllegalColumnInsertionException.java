package it.polimi.ingsw.Exceptions;

public class IllegalColumnInsertionException extends Exception{

    public IllegalColumnInsertionException(String errorMessage) {
        super(errorMessage);
    }

    public IllegalColumnInsertionException() {

    }

}