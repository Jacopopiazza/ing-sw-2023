package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;
import it.polimi.ingsw.Exceptions.*;


public class Square extends GlobalGoal {

    public Square(int people) throws InvalidNumberOfPlayersException {
        super(people);
    }

    // 2 separated groups of 2x2 Tiles of the same color
    @Override
    public boolean check(Shelf s) throws MissingShelfException {
        if (s == null) throw new MissingShelfException();
        int c = s.getColumns();
        int r = s.getRows();
        Coordinates[] firstSquare = new Coordinates[TileColor.values().length];
        for (int i = 0; i < r - 1; i++) {
            for (int j = 0; j < c - 1; j++) {
                Coordinates coord = new Coordinates(i, j);
                Tile temp = s.getTile(coord);
                if (temp != null && (firstSquare[temp.getColor().ordinal()] == null ||
                        (
                                (!coord.equals(new Coordinates(firstSquare[temp.getColor().ordinal()].getX() + 1, firstSquare[temp.getColor().ordinal()].getY())))
                             && (!coord.equals(new Coordinates(firstSquare[temp.getColor().ordinal()].getX() + 1, firstSquare[temp.getColor().ordinal()].getY() + 1)))
                             && (!coord.equals(new Coordinates(firstSquare[temp.getColor().ordinal()].getX(), firstSquare[temp.getColor().ordinal()].getY() + 1)))
                        )
                )
                ) {
                    // then check for the color of all the other tiles
                    if (s.getTile(new Coordinates(i + 1, j)).getColor().equals(temp.getColor())
                            && s.getTile(new Coordinates(i + 1, j + 1)).getColor().equals(temp.getColor())
                            && s.getTile(new Coordinates(i, j + 1)).getColor().equals(temp.getColor())
                    ) {
                        // found a square for this color
                        if (firstSquare[temp.getColor().ordinal()] == null) {
                            firstSquare[temp.getColor().ordinal()] = coord;
                        } else {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
