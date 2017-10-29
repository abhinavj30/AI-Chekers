package client;

public class MoveLocation {

    private final int NO_MOVE = 0;
    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;

    final int moveType;
    final int xSource;
    final int ySource;
    final int xDestination;
    final int yDestination;
    final int identifier;
    final int moveNumber;

    boolean isTerminal() {
        return isTerminal;
    }

    void setTerminal(boolean terminal) {
        isTerminal = terminal;
    }

    private boolean isTerminal;

    MoveLocation(int xLoc, int yLoc, int xDest, int yDest, int moveTypeIn, int id, int moveNum, boolean terminal) {
        xSource = xLoc;
        ySource = yLoc;
        xDestination = xDest;
        yDestination = yDest;
        this.moveType = moveTypeIn;
        identifier = id;
        moveNumber = moveNum;
        isTerminal = terminal;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MoveLocation) {
            MoveLocation loc = (MoveLocation) obj;
            if (xSource == loc.xSource && ySource == loc.ySource && xDestination == loc.xDestination &&
                    yDestination == loc.yDestination && moveType == loc.moveType && identifier == loc.identifier &&
                    moveNumber == loc.moveNumber && isTerminal == loc.isTerminal) {
                return true;
            }
        }
        return false;
    }
}
