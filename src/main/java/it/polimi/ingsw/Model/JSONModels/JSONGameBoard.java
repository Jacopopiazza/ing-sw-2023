package it.polimi.ingsw.Model.JSONModels;

import it.polimi.ingsw.Model.Coordinates;

public record JSONGameBoard (int people, Coordinates[] cells) {
    public JSONGameBoard(int people, Coordinates[] cells){
        this.cells = cells.clone();
        this.people = people;
    }
}