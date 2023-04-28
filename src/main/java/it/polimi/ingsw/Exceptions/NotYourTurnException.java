package it.polimi.ingsw.Exceptions;

public class NotYourTurnException extends Exception {

    public NotYourTurnException(String errorMessage) {
        super(errorMessage);
    }

    public NotYourTurnException() {

    }

}
