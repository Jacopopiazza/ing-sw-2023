package it.polimi.ingsw.Exceptions;

public class EmptyStackException extends Exception{

    public EmptyStackException(String errorMessage) {
        super(errorMessage);
    }

    public EmptyStackException() {

    }

}