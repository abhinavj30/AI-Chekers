package client;

public class MoveLocation extends CheckerLocation {

    private final int NO_MOVE = 0;
    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;

    final int moveType;

    MoveLocation(int xLoc, int yLoc, int moveTypeIn){
        xLocation = xLoc;
        yLocation = yLoc;
        this.moveType = moveTypeIn;
    }
}
