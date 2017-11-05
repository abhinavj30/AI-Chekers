package AI;

import client.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import static client.RunGame.newGame;
import static client.CheckersGame.*;

public class AI {
    final int RED = 0;
    final int BLACK = 1;

    private final int playerNum;

    private long startTime = 0;
    private boolean timeOver = false;

    private final long timeLimit;
    private MoveLocation chosenMove;

    public AI(int playerNum, int timeLimit){
        this.playerNum = playerNum;
        this.timeLimit = timeLimit;
    }

    private MoveLocation aiMove(){
        startTime = (new Date()).getTime();

        for (int depth = 1; depth < 12; depth++){
            alphaBetaSearch(new Board(gameBoard), depth, Long.MIN_VALUE, Long.MAX_VALUE, true, true);
        }
        return chosenMove;
    }

    private long alphaBetaSearch(Board boardIn, int depth, long alphaIn, long betaIn, boolean max, boolean isRoot){
        if (!timeOver){
            if ((new Date()).getTime() - startTime > 0.95*timeLimit){
                timeOver = true;
                return 0;
            }
            long alpha = alphaIn;
            long beta = betaIn;
            long branchValue;

            ArrayList<MoveLocation> moves = new ArrayList<>();
            if (max){
                newGame.checkValidMoves(moves, boardIn, playerNum);
            } else {
                newGame.checkValidMoves(moves, boardIn, (playerNum % 2) + 1);
            }

            if (moves.size() == 0 || depth == 0){
                return calculateHeuristic(boardIn);
            }
            if (max){
                branchValue = Long.MIN_VALUE;
                for (MoveLocation move : moves){
                    Board board = new Board(boardIn);
                    board.pieceMover(move);
                    long childValue = alphaBetaSearch(board,depth - 1, alpha, beta,false, false);
                    if (childValue > branchValue){
                        branchValue = childValue;
                        if (isRoot){
                            chosenMove = new MoveLocation(move);
                        }
                    } else if (childValue == branchValue && isRoot){
                        if ((new Random()).nextBoolean()){
                            chosenMove = new MoveLocation(move);
                        }
                    }
                    if (branchValue > alpha){
                        alpha = childValue;
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
                return branchValue;
            } else {
                branchValue = Long.MAX_VALUE;
                for (MoveLocation move : moves){
                    Board board = new Board(boardIn);
                    board.pieceMover(move);
                    long childValue = alphaBetaSearch(board,depth - 1, alpha, beta,false, false);
                    if (childValue < branchValue){
                        branchValue = childValue;
                    }
                    if (branchValue < beta){
                        beta = childValue;
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
                return branchValue;
            }
        }
        return 0;
    }

    private int calculateHeuristic(Board boardIn){
        Random random = new Random();
        return random.nextInt(10);
    }
}