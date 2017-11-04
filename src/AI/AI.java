package AI;

import client.*;

import java.util.ArrayList;

import static client.Board.*;
import static client.CheckersGame.*;

public class AI {
    final int RED = 0;
    final int BLACK = 1;

    final int playerNum;

    AI(int playerNum){
        this.playerNum = playerNum;
    }


    void searchDown(){
        Checker[][] boardTemp = new Checker[8][8];
        int depth = 0;
        for (MoveLocation move : CheckersGame.validMoves){

        }
    }
}
