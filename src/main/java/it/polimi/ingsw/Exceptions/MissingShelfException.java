package it.polimi.ingsw.Exceptions;

public class MissingShelfException extends Exception{

    public MissingShelfException(String errorMessage) {
        super(errorMessage);
    }

}