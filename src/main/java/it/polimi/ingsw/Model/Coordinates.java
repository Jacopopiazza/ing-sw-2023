package it.polimi.ingsw.Model;

import java.util.Objects;

public class Coordinates {
    private final int x;
    private final int y;

    public Coordinates(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if ( this == o )
            return true;
        if ( o == null || ( this.getClass() != o.getClass() ) )
            return false;
        Coordinates temp = (Coordinates) o;
        return ( x == temp.x ) && ( y == temp.y );
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

}