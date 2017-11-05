package client;

public class CheckerLocation {

    final int xLocation;
    final int yLocation;

    CheckerLocation(int xLoc, int yLoc){
        xLocation = xLoc;
        yLocation = yLoc;
    }

    CheckerLocation(CheckerLocation loc){
        this.xLocation = loc.xLocation;
        this.yLocation = loc.yLocation;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof CheckerLocation) {
            CheckerLocation loc = (CheckerLocation)obj;
            if (xLocation == loc.xLocation && yLocation == loc.yLocation) {
                return true;
            }
        }
        return false;
    }
}
