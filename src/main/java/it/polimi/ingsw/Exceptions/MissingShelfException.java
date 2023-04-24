package it.polimi.ingsw.Exceptions;

public class MissingShelfException extends RuntimeException{

    public MissingShelfException(String errorMessage) {
        super(errorMessage);
    }

    public MissingShelfException() {

    }

}