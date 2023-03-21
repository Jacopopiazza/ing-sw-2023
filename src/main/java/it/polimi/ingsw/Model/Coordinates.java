package it.polimi.ingsw.Model;

import java.util.Objects;

public class Coordinates {
    private final int X;
    private final int Y;

    public Coordinates(int x, int y) {
        this.X = x;
        this.Y = y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || this.getClass() != o.getClass())
            return false;
        Coordinates temp = (Coordinates) o;
        return X == temp.X && Y == temp.Y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(X, Y);
    }
}