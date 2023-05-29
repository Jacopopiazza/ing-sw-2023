package it.polimi.ingsw.ModelView;

import it.polimi.ingsw.Model.Coordinates;
import it.polimi.ingsw.Model.PrivateGoal;

import java.io.Serializable;

public class PrivateGoalView implements Serializable {

    private final Coordinates[] coords;

    public PrivateGoalView(PrivateGoal goal){
        coords = goal.getCoordinates();
    }

    public Coordinates[] getCoordinates(){
        return coords;
    }

    public int getId(){
        return 1; //to be implemented
    }
}
