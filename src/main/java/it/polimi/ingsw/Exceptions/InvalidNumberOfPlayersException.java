package it.polimi.ingsw.Exceptions;

public class InvalidNumberOfPlayersException extends RuntimeException{

    public InvalidNumberOfPlayersException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidNumberOfPlayersException() {

    }

}