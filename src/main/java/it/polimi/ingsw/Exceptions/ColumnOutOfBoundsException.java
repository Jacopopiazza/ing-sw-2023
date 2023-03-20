package it.polimi.ingsw.Exceptions;

public class ColumnOutOfBoundsException extends RuntimeException{

    public ColumnOutOfBoundsException(String errorMessage) {
        super(errorMessage);
    }

    public ColumnOutOfBoundsException() {

    }

}