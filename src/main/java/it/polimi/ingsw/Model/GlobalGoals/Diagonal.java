package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.GlobalGoal;
import it.polimi.ingsw.Model.Shelf;
import it.polimi.ingsw.Exceptions.*;


public class Diagonal extends GlobalGoal {

    @Override
    public boolean check(Shelf s)  throws MissingShelfException {
        if(s==null) throw new MissingShelfException();


    }
}
