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

    @Override
    public boolean equals(Object obj){
        if (obj instanceof MoveLocation) {
            MoveLocation loc = (MoveLocation)obj;
            if (xLocation == loc.xLocation && yLocation == loc.yLocation && moveType == loc.moveType) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
