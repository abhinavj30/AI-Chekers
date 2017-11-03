package client;

import java.util.ArrayList;

public class MoveLocation {

    private final int NO_MOVE = 0;
    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;

    final int moveType;
    final int xSource;
    final int ySource;
    final int xDestination;
    final int yDestination;
    ArrayList<CheckerLocation> jumps;

    MoveLocation(int xLoc, int yLoc, int xDest, int yDest, int moveTypeIn) {
        xSource = xLoc;
        ySource = yLoc;
        xDestination = xDest;
        yDestination = yDest;
        moveType = moveTypeIn;
        jumps = new ArrayList<>();
    }

    MoveLocation(MoveLocation loc){
        this.moveType = loc.moveType;
        this.xSource = loc.xSource;
        this.ySource = loc.ySource;
        this.xDestination = loc.xDestination;
        this.yDestination = loc.yDestination;
        this.jumps = loc.jumps;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MoveLocation) {
            MoveLocation loc = (MoveLocation) obj;
            if (xSource == loc.xSource && ySource == loc.ySource && xDestination == loc.xDestination &&
                    yDestination == loc.yDestination && moveType == loc.moveType) {
                return true;
            }
        }
        return false;
    }
}
