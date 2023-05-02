package it.polimi.ingsw.Messages;

import it.polimi.ingsw.Model.Coordinates;

public class TurnActionMessage implements Message {
    private String nickname;
    private Coordinates[] chosenTiles;
    private int column;

    public TurnActionMessage(String nickname, Coordinates[] chosenTiles, int column){
        this.nickname = nickname;
        this.chosenTiles = chosenTiles;
        this.column = column;
    }
}
