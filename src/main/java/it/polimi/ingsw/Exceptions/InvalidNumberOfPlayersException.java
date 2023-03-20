package it.polimi.ingsw.Exceptions;

public class InvalidNumberOfPlayersException extends Exception{

    public InvalidNumberOfPlayersException(String errorMessage) {
        super(errorMessage);
    }

    public InvalidNumberOfPlayersException() {

    }

}