package it.polimi.ingsw.Exceptions;

public class ColumnOutOfBoundsException extends Exception{

    public ColumnOutOfBoundsException(String errorMessage) {
        super(errorMessage);
    }

    public ColumnOutOfBoundsException() {

    }

}