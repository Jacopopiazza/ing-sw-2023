package it.polimi.ingsw.Model;

import java.util.Objects;

public class Coordinates {
    private final int ROW;
    private final int COL;

    public Coordinates(int r, int c) {
        this.ROW = r;
        this.COL = c;
    }

    public int getROW() {
        return ROW;
    }

    public int getCOL() {
        return COL;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o )
            return true;
        if ( o == null || ( this.getClass() != o.getClass() ) )
            return false;
        Coordinates temp = (Coordinates) o;
        return ( ROW == temp.ROW ) && ( COL == temp.COL );
    }

    @Override
    public int hashCode() {
        return Objects.hash(ROW, COL);
    }

}