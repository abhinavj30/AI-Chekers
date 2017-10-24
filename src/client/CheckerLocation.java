package client;

import com.sun.org.apache.xpath.internal.operations.Equals;

public class CheckerLocation {

    int xLocation;
    int yLocation;

    CheckerLocation(int xLoc, int yLoc){
        xLocation = xLoc;
        yLocation = yLoc;
    }

    CheckerLocation(){
        xLocation = 0;
        yLocation = 0;
    }

    @Override
    public boolean equals(Object obj){
        if (obj instanceof CheckerLocation) {
            CheckerLocation loc = (CheckerLocation)obj;
            if (xLocation == loc.xLocation && yLocation == loc.yLocation) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
