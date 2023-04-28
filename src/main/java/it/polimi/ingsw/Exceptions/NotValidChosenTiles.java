package it.polimi.ingsw.Exceptions;

public class NotValidChosenTiles extends Throwable {

    public NotValidChosenTiles(String errorMessage) {
        super(errorMessage);
    }

    public NotValidChosenTiles() {

    }

}
