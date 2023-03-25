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
        int c = Shelf.getColumns();
        int r = Shelf.getRows();
        Coordinates firstSquare = null;

        if (s == null){
            throw new MissingShelfException();
        }

        for (int i = 0; i < r - 1; i++) {
            for (int j = 0; j < c - 1; j++) {
                Coordinates coord = new Coordinates(i, j);
                Tile temp = s.getTile(coord);
                //here I will check if there is a Tile at this coordinates and, if this is the case,
                //I will check whether that Tile is not contained in the first found square (if it was already found)
                if (temp != null && (firstSquare == null ||
                        (
                                (!coord.equals(new Coordinates(firstSquare.getX() + 1, firstSquare.getY())))
                             && (!coord.equals(new Coordinates(firstSquare.getX() + 1, firstSquare.getY() + 1)))
                             && (!coord.equals(new Coordinates(firstSquare.getX(), firstSquare.getY() + 1)))
                        )
                )
                ) {
                    // here I will check if there is a square around this Tile
                    if (       s.getTile(new Coordinates(i + 1, j)).getColor().equals(temp.getColor())
                            && s.getTile(new Coordinates(i + 1, j + 1)).getColor().equals(temp.getColor())
                            && s.getTile(new Coordinates(i, j + 1)).getColor().equals(temp.getColor())
                    ) {
                        // here I will check whether the Tiles around the square have a different color
                        if(
                                (
                                        i - 1 < 0 || (
                                                   (s.getTile(new Coordinates(i - 1, j)) == null  || !s.getTile(new Coordinates(i - 1, j)).getColor().equals(temp.getColor()))
                                                && (s.getTile(new Coordinates(i - 1, j + 1)) == null  || !s.getTile(new Coordinates(i - 1, j + 1)).getColor().equals(temp.getColor()))
                                        )
                                ) &&
                                (
                                        j - 1 < 0 || (
                                                   (s.getTile(new Coordinates(i, j - 1)) == null  || !s.getTile(new Coordinates(i, j - 1)).getColor().equals(temp.getColor()))
                                                && (s.getTile(new Coordinates(i + 1, j - 1)) == null  || !s.getTile(new Coordinates(i + 1, j - 1)).getColor().equals(temp.getColor()))
                                        )
                                ) &&
                                (
                                        i + 2 >= r || (
                                                   (s.getTile(new Coordinates(i + 2, j)) == null  || !s.getTile(new Coordinates(i + 2, j)).getColor().equals(temp.getColor()))
                                                && (s.getTile(new Coordinates(i + 2, j + 1)) == null  || !s.getTile(new Coordinates(i + 2, j + 1)).getColor().equals(temp.getColor()))
                                        )
                                ) &&
                                (
                                        j + 2 >= c || (
                                                   (s.getTile(new Coordinates(i, j + 2)) == null  || !s.getTile(new Coordinates(i, j + 2)).getColor().equals(temp.getColor()))
                                                && (s.getTile(new Coordinates(i + 1, j + 2)) == null  || !s.getTile(new Coordinates(i + 1, j + 2)).getColor().equals(temp.getColor()))
                                        )
                                )

                        ) {
                            if (firstSquare == null) {
                                firstSquare = coord;
                            } else {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}
