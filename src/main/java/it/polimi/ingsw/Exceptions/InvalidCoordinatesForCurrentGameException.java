package it.polimi.ingsw.Exceptions;

public class InvalidCoordinatesForCurrentGameException extends Exception{

    public InvalidCoordinatesForCurrentGameException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidCoordinatesForCurrentGameException() {

    }

}