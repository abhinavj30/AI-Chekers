package client;

import java.util.ArrayList;

public class Move {

    private final int NO_MOVE = 0;
    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;

    final int moveType;
    final int xSource;
    final int ySource;
    final int xDestination;
    final int yDestination;
    ArrayList<CheckerLocation> jumps;

    Move(int xLoc, int yLoc, int xDest, int yDest, int moveTypeIn) {
        xSource = xLoc;
        ySource = yLoc;
        xDestination = xDest;
        yDestination = yDest;
        moveType = moveTypeIn;
        jumps = new ArrayList<>();
    }

    public Move(Move move){
        this.moveType = move.moveType;
        this.xSource = move.xSource;
        this.ySource = move.ySource;
        this.xDestination = move.xDestination;
        this.yDestination = move.yDestination;
        jumps = new ArrayList<>();
        for (CheckerLocation loc : move.jumps){
            this.jumps.add(new CheckerLocation(loc));
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move loc = (Move) obj;
            if (xSource == loc.xSource && ySource == loc.ySource && xDestination == loc.xDestination &&
                    yDestination == loc.yDestination && moveType == loc.moveType && equalsJumps(loc.jumps)) {
                return true;
            }
        }
        return false;
    }

    private boolean equalsJumps (ArrayList<CheckerLocation> jumpsIn){
        for (int i = 0; i < jumpsIn.size(); i++){
            if (jumps.size() != jumpsIn.size()){
                return false;
            }
            if (jumpsIn.get(i) != jumpsIn.get(i)){
                return false;
            }
        }
        return true;
    }

    Move(){
        xSource = -1;
        ySource = -1;
        xDestination = -1;
        yDestination = -1;
        moveType = NO_MOVE;
        jumps = new ArrayList<>();
    }

    @Override
    public String toString(){
        StringBuilder retString = new StringBuilder();
        retString.append("move: ");
        retString.append(xSource + "," + ySource + " -");
        for (CheckerLocation loc : jumps){
            retString.append("- " + loc.xLocation + "," + loc.yLocation + " ");
        }
        return retString.toString();
    }
}
