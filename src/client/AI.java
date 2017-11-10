package client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import static client.CheckersGame.*;

class AI {

    private final int BLACK = 1;
    private final int RED = 2;

    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;
    private final int[] moveDirections = {1, 3, 11, 13};

    private final int playerNum;

    private long startTime = 0;
    private boolean timeOver = false;

    private final long timeLimit;

    private int currDepth = 0;

    private long heuristicCalc;
    private long boardsCalc;

    private Move chosenMove;

    AI(int playerNum, int timeLimit) {
        this.playerNum = playerNum;
        this.timeLimit = timeLimit;
    }

    Move aiMove() {
        timeOver = false;
        System.out.println("AI making move...");
        startTime = (new Date()).getTime();

        heuristicCalc = 0;
        boardsCalc = 0;

        ArrayList<Move> moveList = new ArrayList<>();
        Board testBoard = new Board(gameBoard);

        try {
            checkValidMoves(moveList, testBoard, playerNum);
        } catch (Exception e) {
            System.out.println("Error...");
            System.err.println(e.getMessage());
        }

//        System.out.println("Moves from AI");
//
//        for (Move move: moveList){
//            System.out.println(move);
//        }

        if (moveList.size() == 1) {
            System.out.println("AI making default move");
            return moveList.get(0);
        }
        Move returnMove = new Move();
        int depth;
        for (depth = 6; depth < 15; depth++) {
            currDepth = depth;
            chosenMove = new Move();
            System.gc();
            alphaBetaSearch(new Board(gameBoard), depth, Long.MIN_VALUE, Long.MAX_VALUE, true, true);
            if (timeOver) {
                System.out.println("Time ran out after searching at depth " + (depth - 1));
                break;
            }
            if (!timeOver) {
                returnMove = new Move(chosenMove);
            }
        }
        if (!timeOver) {
            System.out.println("Searched till depth " + depth + " in " + ((new Date()).getTime() - startTime) + " ms");
        }
//        System.out.println("Leaves viewed: " + heuristicCalc);
//        System.out.println("Nodes viewed: " + boardsCalc);
        if (returnMove.moveType == 0) {
            return null;
        }
        return returnMove;
    }

    private Move pickMove(ArrayList<MoveOption> moveOptions){
        int chosenIndex = 0;
        long maxValue = 0;
        for (int i = 0; i < moveOptions.size(); i++){
            if (moveOptions.get(i).getValue() > maxValue){
                maxValue = moveOptions.get(i).getValue();
                chosenIndex = i;
            }
        }
        return moveOptions.get(chosenIndex).getMove();
    }

    void printMove(Move move) {
        System.out.print("Move: " + move.xSource + "," + move.ySource);
        if (move.jumps.size() == 0){
            System.out.print(" - " + move.xDestination + "," + move.yDestination);
        }
        for (CheckerLocation loc : move.jumps) {
            System.out.print(" - " + loc.xLocation + "," + loc.yLocation);
        }
        System.out.println();
    }


    private void checkValidMoves(ArrayList<Move> moveList, Board boardIn, int player) {
        boolean killAvailable = false;
        ArrayList<CheckerLocation> currentCheckers = new ArrayList<>();
        if (player == BLACK) {
            currentCheckers = boardIn.getBlackPieceLocations();
        } else if (player == RED) {
            currentCheckers = boardIn.getRedPieceLocations();
        }
        for (CheckerLocation loc : currentCheckers) {
            checkSquareMoves(loc, boardIn, false, null, player, moveList, boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing());
            //checkSquareMoves(loc, false, 0, null, player, boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing(), moveList, boardIn);
        }
        for (Move move : moveList) {
            if (move.moveType == MOVE_KILL) {
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

    private void checkSquareMoves(CheckerLocation location, Board boardIn, boolean continueKill, Move moveIn, int playerNum, ArrayList<Move> moveList, boolean isKing){
        boolean noMoreJumps = true;
        for (int direc : moveDirections){
            Move move = boardIn.checkMove(direc, location.xLocation, location.yLocation, playerNum, isKing, continueKill);
            if (move != null){
                if (move.moveType == MOVE_BLANK){
                    move.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                    moveList.add(new Move(move));
                } else {
                    Board boardOut = new Board(boardIn);
                    Move moveOut;
                    CheckerLocation dest = new CheckerLocation(move.xDestination, move.yDestination);
                    move.jumps.add(dest);
                    boardOut.pieceMover(move, true);
                    if (!continueKill){
                        moveOut = new Move(move);
                    } else {
                        moveOut = new Move(moveIn);
                        moveOut.jumps.add(dest);
                    }
                    noMoreJumps = false;
                    checkSquareMoves(dest, boardOut, true, moveOut, playerNum, moveList, isKing);
                }
            }
        }
        if (noMoreJumps && continueKill){
            moveList.add(new Move(moveIn));
        }
    }

    private long alphaBetaSearch(Board boardIn, int depth, long alphaIn, long betaIn, boolean max, boolean isRoot) {
        if (!timeOver) {
            boardsCalc++;
            if ((new Date()).getTime() - startTime > timeLimit * 1000) {
                timeOver = true;
                return 0;
            }
            long alpha = alphaIn;
            long beta = betaIn;

            ArrayList<Move> moves = new ArrayList<>();
            if (max) {
                checkValidMoves(moves, boardIn, playerNum);
            } else {
                if (playerNum == BLACK){
                    checkValidMoves(moves, boardIn, RED);
                } else {
                    checkValidMoves(moves, boardIn, BLACK);
                }
            }

            if (moves.size() == 0 || depth == 0) {
                long heuristic = calculateHeuristic(boardIn, depth);
                //System.out.println("Heuristic = " + heuristic);
                //return calculateHeuristic(boardIn);
                return heuristic;
            }
            if (max) {
                long parentValue = Long.MIN_VALUE;
                for (Move move : moves) {
                    Board board = new Board(boardIn);
                    board.pieceMover(move, false);
                    long childValue = alphaBetaSearch(board, depth - 1, alpha, beta, false, false);
                    if (childValue > parentValue) {
                        parentValue = childValue;
                        if (isRoot){
                            chosenMove = new Move(move);
                        }
                    }
                    if (parentValue > alpha) {
                        alpha = parentValue;
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
                return parentValue;
            } else {
                long parentValue = Long.MAX_VALUE;
                for (Move move : moves) {
                    Board board = new Board(boardIn);
                    board.pieceMover(move, false);
                    long childValue = alphaBetaSearch(board, depth - 1, alpha, beta, true, false);
                    if (childValue < parentValue) {
                        parentValue = childValue;
                    }
                    if (parentValue < beta) {
                        beta = parentValue;
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
                return parentValue;
            }
        }
        return 0;
    }

    private long calculateHeuristic(Board boardIn, int depthIn) {
        if (playerNum == BLACK){
            if (boardIn.getRedPieceLocations().size() == 0){
                return Long.MAX_VALUE - 1 - ((currDepth - depthIn) * 10000) - (new Random()).nextInt(100);
            } else if (boardIn.getBlackPieceLocations().size() == 0){
                return Long.MIN_VALUE + 1 + ((currDepth - depthIn) * 10000) + (new Random()).nextInt(100);
            }
        } else {
            if (boardIn.getBlackPieceLocations().size() == 0){
                return Long.MAX_VALUE - 1 - ((currDepth - depthIn) * 10000) - (new Random()).nextInt(100);
            } else if (boardIn.getRedPieceLocations().size() == 0){
                return Long.MIN_VALUE + 1 + ((currDepth - depthIn) * 10000) + (new Random()).nextInt(100);
            }
        }
        long numPieces = numPiecesValue(boardIn, playerNum);
        long avgToKing = (kingDistance(boardIn, (playerNum % 2) + 1) - kingDistance(boardIn, playerNum) + 7) * 99 / 7;
        long piecesLeft = piecesLeftWeight(boardIn, playerNum);
        long kingLoc = kingLocation(boardIn, playerNum);
        long randomSafety = (new Random()).nextInt(9);
        heuristicCalc++;
        return (numPieces * 10000000) + (avgToKing * 100000) + (piecesLeft * 1000) + (kingLoc * 10) + (randomSafety);
    }

    private long numPiecesValue(Board boardIn, int playerNum){
        int myPieces = 0;
        int enemyPieces = 0;
        if (playerNum == BLACK){
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                myPieces += (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) ? 5 : 3;
            }
            for (CheckerLocation loc : boardIn.getRedPieceLocations()){
                enemyPieces += (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) ? 5 : 3;
            }
        } else {
            for (CheckerLocation loc : boardIn.getRedPieceLocations()){
                myPieces += (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) ? 5 : 3;
            }
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                enemyPieces += (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) ? 5 : 3;
            }
        }
        return myPieces - enemyPieces;
    }

    private long kingDistance (Board boardIn, int playerNum){
        int myToKingVal = 0;
        int enemyToKingVal = 0;
        int numMyPawns = 0;
        int numEnemyPawns = 0;
        if (playerNum == BLACK){
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                if (!boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) {
                    myToKingVal += (7 - loc.xLocation);
                    numMyPawns++;
                }
            }
            for (CheckerLocation loc : boardIn.getRedPieceLocations()){
                if (!boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) {
                    enemyToKingVal += (loc.xLocation);
                    numEnemyPawns++;
                }
            }
        } else {
            for (CheckerLocation loc : boardIn.getRedPieceLocations()){
                if (!boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) {
                    myToKingVal += (loc.xLocation);
                    numMyPawns++;
                }
            }
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                if (!boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) {
                    enemyToKingVal += (7 - loc.xLocation);
                    numEnemyPawns++;
                }
            }
        }
        if (numMyPawns == 0){
            return 0;
        } else if (numEnemyPawns == 0) {
            return myToKingVal / numMyPawns;
        } else {
            return ((myToKingVal / numMyPawns) - (enemyToKingVal / numEnemyPawns)) * 99 / 7;
        }
    }

    private long piecesLeftWeight (Board boardIn, int playerNum){
        int numPieces = boardIn.getBlackPieceLocations().size() + boardIn.getRedPieceLocations().size();
        int blackPieces = gameBoard.getBlackPieceLocations().size();
        int redPieces = gameBoard.getRedPieceLocations().size();
        if (playerNum == BLACK){
            if (blackPieces > redPieces){
                return (24 - numPieces) * 99 / 24;
            } else {
                return (numPieces) * 99 / 24;
            }
        } else {
            if (redPieces > blackPieces){
                return (24 - numPieces) * 99 / 24;
            } else {
                return (numPieces) * 99 /24;
            }
        }
    }

    private long kingLocation (Board boardIn, int playerNum){
        int myKings = 0;
        int enemyKings = 0;
        int myKingsNum = 0;
        int enemyKingsNum = 0;
        if (playerNum == BLACK){
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                if (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()){
                    myKings += Math.abs(loc.xLocation - loc.yLocation);
                    myKingsNum++;
                }
            }
            for (CheckerLocation loc : boardIn.getRedPieceLocations()){
                if (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()){
                    enemyKings += Math.abs(loc.xLocation - loc.yLocation);
                    enemyKingsNum++;
                }
            }
        } else {
            for (CheckerLocation loc : boardIn.getRedPieceLocations()){
                if (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()){
                    myKings += Math.abs(loc.xLocation - loc.yLocation);
                    myKingsNum++;
                }
            }
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                if (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()){
                    enemyKings += Math.abs(loc.xLocation - loc.yLocation);
                    enemyKingsNum++;
                }
            }
        }
        if (myKingsNum == 0 || enemyKingsNum == 0){
            return 0;
        } else {
            myKings = 7 - (myKings / myKingsNum);
            enemyKings = 7 - (enemyKings / enemyKingsNum);
            return (myKings - enemyKings) * 16;
        }
    }
}