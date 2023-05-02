package it.polimi.ingsw.Exceptions;

public class InvalidCoordinatesForCurrentGameException extends RuntimeException{

    public InvalidCoordinatesForCurrentGameException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidCoordinatesForCurrentGameException() {

    }

}