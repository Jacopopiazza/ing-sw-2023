package it.polimi.ingsw.Model;

public class GameBoard {
    private Map<Coordinates, Optional<Tile>> board;
    private int tilesOnBoard;
    private GameBoard(){}
    public static GameBoard gameBoardTwo(){}
    public static GameBoard gameBoardThree(){}
    public static GameBoard gameBoardFour(){}

    private void makeItTwo(){}
    private void makeItThree(){}
    private void makeItFour(){}
    public int getNumberOfTiles(){}
    public Set<Coordinates> getCoords(){}
    public Optional<Optional<Tile>>  getTile(Coordinates c){}
    public void setTile(Coordinates c, Tile t){}
    public Optional<Tile> pickTile(Coordinates c){}

}
