package client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import static client.CheckersGame.*;

public class AI {

    final int BLACK = 1;
    final int RED = 2;

    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;
    private final int[] moveDirections = {1, 3, 11, 13};

    private final int playerNum;

    private long startTime = 0;
    private boolean timeOver = false;

    private final long timeLimit;
    private Move chosenMove;

    public AI(int playerNum, int timeLimit){
        this.playerNum = playerNum;
        this.timeLimit = timeLimit;
    }

    public Move aiMove(){
        timeOver = false;
        System.out.println("AI making move...");
        chosenMove = null;
        startTime = (new Date()).getTime();

        ArrayList<Move> tempList = new ArrayList<>();
        Board testBoard = new Board(gameBoard);

        try {
            checkValidMoves(tempList, testBoard, 1);
        } catch (Exception e){
            System.out.println("Error...");
            System.err.println(e.getMessage());
        }


        System.out.println("Moves found in AI: " + tempList.size());

        if (tempList.size() == 1){
            return tempList.get(0);
        }
        Move returnMove = new Move();
        for (int depth = 5; depth < 15; depth++){
            System.gc();
            System.out.println("Searching at depth " + depth);
            if (timeOver){
                System.out.println("Time's up...");
            }
            alphaBetaSearch(new Board(gameBoard), depth, Long.MIN_VALUE, Long.MAX_VALUE, true, true);
            if (!timeOver){
                returnMove = new Move(chosenMove);
            }
        }
        if (returnMove.moveType == 0){
            return null;
        }
        return chosenMove;
    }



    public void checkValidMoves(ArrayList<Move> moveList, Board boardIn, int player) {
        boolean killAvailable = false;
        ArrayList<CheckerLocation> currentCheckers = new ArrayList<>();
        if (player == BLACK) {
            currentCheckers = boardIn.blackPieceLocations;
        } else if (player == RED) {
            currentCheckers = boardIn.redPieceLocations;
        }
        for (CheckerLocation loc : currentCheckers) {
            checkSquareMoves(loc, false, 0, null, player, boardIn.boardPieces[loc.xLocation][loc.yLocation].isKing(), moveList, boardIn);
        }
        for (Move move : moveList) {
            if (move.moveType == MOVE_KILL){
                killAvailable = true;
            }
        }
        if (killAvailable) {
            Iterator<Move> iter = moveList.iterator();
            while (iter.hasNext()) {
                Move move = iter.next();
                if (move.moveType == MOVE_BLANK) {
                    iter.remove();
                }
            }
        }
    }

    public void checkSquareMoves(CheckerLocation location, boolean continueKill, int prevDirection, Move moveIn, int playerNum, boolean isKing, ArrayList<Move> moveList, Board boardIn) {
        boolean noMoreJumps = true;
        for (int direc : moveDirections) {
            if (!continueKill || direc != getOppositeDirection(prevDirection)) {
                Move move = boardIn.checkMove(direc, location.xLocation, location.yLocation, playerNum, isKing, continueKill);
                if (move != null) {
                    if (move.moveType == MOVE_KILL) {
                        Move moveOut;
                        if (!continueKill) {
                            moveOut = move;
                        } else {
                            moveOut = new Move(moveIn);
                        }
                        moveOut.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                        checkSquareMoves(moveOut.jumps.get(moveOut.jumps.size() - 1), true, direc, moveOut, playerNum, isKing, moveList, boardIn);
                        noMoreJumps = false;
                    } else {
                        move.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                        moveList.add(move);
                    }
                }
            }
        }
        if (noMoreJumps && continueKill) {
            moveList.add(moveIn);
        }
    }

    private int getOppositeDirection(int direction) {
        int oppDirec = 1;
        if (direction > 10) {
            oppDirec += 10;
        }
        if (direction % 10 == 1) {
            oppDirec += 2;
        }
        return oppDirec;
    }

    private long alphaBetaSearch(Board boardIn, int depth, long alphaIn, long betaIn, boolean max, boolean isRoot){
        if (!timeOver){
            if ((new Date()).getTime() - startTime > timeLimit*1000){
                timeOver = true;
                return 0;
            }
            long alpha = alphaIn;
            long beta = betaIn;
            long branchValue;

            ArrayList<Move> moves = new ArrayList<>();
            if (max){
                checkValidMoves(moves, boardIn, playerNum);
            } else {
                checkValidMoves(moves, boardIn, (playerNum % 2) + 1);
            }

            if (moves.size() == 0 || depth == 0){
                return calculateHeuristic(boardIn);
            }
            if (max){
                branchValue = Long.MIN_VALUE;
                for (Move move : moves){
                    Board board = new Board(boardIn);
                    board.pieceMover(move);
                    long childValue = alphaBetaSearch(board,depth - 1, alpha, beta,false, false);
                    if (childValue > branchValue){
                        branchValue = childValue;
                        if (isRoot){
                            chosenMove = new Move(move);
                        }
                    } else if (childValue == branchValue && isRoot){
                        if ((new Random()).nextBoolean()){
                            chosenMove = new Move(move);
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
                for (Move move : moves){
                    Board board = new Board(boardIn);
                    board.pieceMover(move);
                    long childValue = alphaBetaSearch(board,depth - 1, alpha, beta,true, false);
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