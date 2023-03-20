package it.polimi.ingsw.Model.GlobalGoals;

import it.polimi.ingsw.Model.*;

public class Square extends PublicGoal {

    // 2 separated groups of 2x2 Tiles of the same color
    @Override
    public boolean check(Shelf s) {
        int c = s.getColumns();
        int r = s.getRows();
        Coordinates firstSquare = null;
        TileColor color = null;
        for(int i = 0; i < r - 1; i++){
            for(int j = 0; j < c - 1; j++){
                Coordinates coord = new Coordinates(i, j);
                Tile temp = s.getTile(coord);
                if(temp != null && (firstSquare == null ||
                        (color == temp.getColor()
                            && (!coord.equals(new Coordinates(firstSquare.getX() + 1, firstSquare.getY())))
                            && (!coord.equals(new Coordinates(firstSquare.getX() + 1, firstSquare.getY() + 1)))
                            && (!coord.equals(new Coordinates(firstSquare.getX(), firstSquare.getY() + 1)))
                        )
                    )
                ) {
                    // then check for the color of all the other tiles
                    if(s.getTile(new Coordinates(firstSquare.getX() + 1, firstSquare.getY())).getColor().equals(temp.getColor())
                        && s.getTile(new Coordinates(firstSquare.getX() + 1, firstSquare.getY() + 1)).getColor().equals(temp.getColor())
                        && s.getTile(new Coordinates(firstSquare.getX(), firstSquare.getY() + 1)).getColor().equals(temp.getColor())
                    ){
                        // found the first square, set the variables to find the second square: coordinates and color
                        if(firstSquare == null){
                            firstSquare = coord;
                            color = temp.getColor();
                        }else{
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
