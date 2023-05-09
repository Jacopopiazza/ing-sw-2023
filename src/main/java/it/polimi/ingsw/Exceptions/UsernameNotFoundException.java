package it.polimi.ingsw.Exceptions;

public class UsernameNotFoundException extends Exception{

    public UsernameNotFoundException(String errorMessage) {
        super(errorMessage);
    }

    public UsernameNotFoundException(){

    }

}
