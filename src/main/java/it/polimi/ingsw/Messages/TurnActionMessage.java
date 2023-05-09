package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Model.Coordinates;

public class TurnActionMessage implements Message {
    private String username;
    private Coordinates[] chosenTiles;
    private int column;

    public TurnActionMessage(String u, Coordinates[] chosenTiles, int column){
        this.username = u;
        this.chosenTiles = chosenTiles;
        this.column = column;
    }

    public String getUsername() {
        return username;
    }

    public Coordinates[] getChosenTiles() {
        return chosenTiles;
    }

    public int getColumn() {
        return column;
    }


}
