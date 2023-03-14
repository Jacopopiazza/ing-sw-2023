package it.polimi.ingsw.Model;

public class Coordinates {
    // Coordinates of the tile in the shelf and others
    private final int x;
    private final int y;

    public Coordinates(int x, int y){
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o){
        if(o == this){
            return true;
        }
        if(!(o instanceof Coordinates)){
            return false;
        }
        Coordinates coords = (Coordinates) o;
        return this.x == coords.x && this.y == coords.y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
