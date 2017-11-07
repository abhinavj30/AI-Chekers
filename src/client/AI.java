package client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import static client.CheckersGame.*;
import static client.RunGame.newGame;

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

    private ArrayList<MoveOption> moveOptions;


    AI(int playerNum, int timeLimit) {
        this.playerNum = playerNum;
        this.timeLimit = timeLimit;
    }

    Move aiMove() {
        timeOver = false;
        System.out.println("AI making move...");
        startTime = (new Date()).getTime();

        ArrayList<Move> moveList = new ArrayList<>();
        Board testBoard = new Board(gameBoard);

        try {
            checkValidMoves(moveList, testBoard, playerNum);
        } catch (Exception e) {
            System.out.println("Error...");
            System.err.println(e.getMessage());
        }

        System.out.println("Moves found in AI: " + moveList.size());

        for (Move move : moveList){
            printMove(move);
        }

        if (moveList.size() == 1) {
            return moveList.get(0);
        }
        Move returnMove = new Move();

        for (int depth = 6; depth < 20; depth++) {
            moveOptions = new ArrayList<>();
            System.gc();
            System.out.println("Searching at depth " + depth);
            if (timeOver) {
                System.out.println("Time's up...");
                break;
            }
            alphaBetaSearch(new Board(gameBoard), depth, Long.MIN_VALUE, Long.MAX_VALUE, true, true);
            if (!timeOver) {
                returnMove = new Move(pickMove(moveOptions));
            }
        }
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



    private Move pickMove2(ArrayList<MoveOption> moveOptions){
        long maxValue = Long.MIN_VALUE;
        long minValue = Long.MAX_VALUE;
        ArrayList<Move> pickedMoves = new ArrayList<>();
        for (MoveOption option : moveOptions){
            if (option.getMove() == null) {
                System.out.println("move is null...");
                System.exit(-1);
            }
            newGame.printMove(option.getMove());
            System.out.println("Move value: " + option.getValue());
            if (option.getValue() > maxValue){
                maxValue = option.getValue();
            } else if (option.getValue() < minValue){
                minValue = option.getValue();
            }
        }
        System.out.println(minValue + "" + maxValue);
        long threshold = maxValue - ((maxValue - minValue)/10);
        for (MoveOption option : moveOptions){
            if (option.getValue() >= threshold){
                pickedMoves.add(option.getMove());
            }
        }
        Move retMove = pickedMoves.get((new Random()).nextInt(pickedMoves.size()));
        System.out.print("Move picked: ");
        newGame.printMove(retMove);
        return retMove;
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

    private void checkSquareMoves(CheckerLocation location, boolean continueKill, int prevDirection, Move moveIn, int playerNum, boolean isKing, ArrayList<Move> moveList, Board boardIn) {
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
                            boolean stuckInLoop = false;
                            for (CheckerLocation loc : moveIn.jumps){
                                if (move.xDestination == loc.xLocation && move.yDestination == loc.yLocation){
                                    stuckInLoop = true;
                                }
                            }
                            if ((move.xDestination == moveIn.xSource && move.yDestination == moveIn.ySource) || stuckInLoop){
                                moveOut = new Move(moveIn);
                                moveOut.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                                return;
                            }
                            moveOut = new Move(moveIn);
                        }
                        moveOut.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                        try {
                            checkSquareMoves(moveOut.jumps.get(moveOut.jumps.size() - 1), true, direc, moveOut, playerNum, isKing, moveList, boardIn);
                        } catch (StackOverflowError e) {
                            System.out.println("Found error");
                            for (int i = 0; i < 8; i++) {
                                System.out.println();
                                for (int j = 0; j < 8; j++) {
                                    System.out.print(boardIn.getBoardPieces()[i][j].getPieceColor());
                                }
                            }
                            System.out.println();
                            printMove(moveOut);
                            System.exit(-1);

                        }
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

    private long alphaBetaSearch(Board boardIn, int depth, long alphaIn, long betaIn, boolean max, boolean isRoot) {
        if (!timeOver) {
            if ((new Date()).getTime() - startTime > timeLimit * 1000) {
                timeOver = true;
                return 0;
            }
            long alpha = alphaIn;
            long beta = betaIn;
            long branchValue;

            ArrayList<Move> moves = new ArrayList<>();
            if (max) {
                checkValidMoves(moves, boardIn, playerNum);
            } else {
                checkValidMoves(moves, boardIn, (playerNum % 2) + 1);
            }

            if (moves.size() == 0 || depth == 0) {
                return calculateHeuristic(boardIn);
            }
            if (max) {
                branchValue = Long.MIN_VALUE;
                for (Move move : moves) {
                    Board board = new Board(boardIn);
                    board.pieceMover(move, false);
                    long childValue = alphaBetaSearch(board, depth - 1, alpha, beta, false, false);
                    if (childValue > branchValue) {
                        branchValue = childValue;
                    }
                    if (branchValue > alpha) {
                        alpha = childValue;
                    }
                    if (isRoot){
                        moveOptions.add(new MoveOption(move, childValue));
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
                return branchValue;
            } else {
                branchValue = Long.MAX_VALUE;
                for (Move move : moves) {
                    Board board = new Board(boardIn);
                    board.pieceMover(move, false);
                    long childValue = alphaBetaSearch(board, depth - 1, alpha, beta, true, false);
                    if (childValue < branchValue) {
                        branchValue = childValue;
                    }
                    if (branchValue < beta) {
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

    private int calculateHeuristic(Board boardIn) {
        long retVal = 0;
        if (playerNum == BLACK){
            retVal += numPiecesValue(boardIn, playerNum);
            retVal -= numPiecesValue(boardIn, (playerNum % 2) + 1);
        }
        Random random = new Random();
        return random.nextInt(10);
    }

    private long numPiecesValue(Board boardIn, int playerNum){
        long retVal = 0;
        if (playerNum == BLACK){
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                retVal += (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) ? 5 : 3;
            }
        } else {
            for (CheckerLocation loc : boardIn.getRedPieceLocations()){
                retVal += (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) ? 5 : 3;
            }
        }
        return retVal;
    }

    private long kingDistance (Board boardIn, int playerNum){
        long retVal = 0;
        if (playerNum == BLACK){
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                retVal += (7 - loc.xLocation);
            }
            retVal = retVal / boardIn.getBlackPieceLocations().size();
        }
        return retVal;
    }
}


/*

package client;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

import static client.CheckersGame.*;
import static client.RunGame.newGame;

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

    private ArrayList<MoveOption> moveOptions;


    AI(int playerNum, int timeLimit) {
        this.playerNum = playerNum;
        this.timeLimit = timeLimit;
    }

    Move aiMove() {
        timeOver = false;
        System.out.println("AI making move...");
        startTime = (new Date()).getTime();

        ArrayList<Move> moveList = new ArrayList<>();
        Board testBoard = new Board(gameBoard);

        //newGame.checkValidMoves(moveList, testBoard, playerNum);
        try {
            checkValidMoves(moveList, testBoard, playerNum);
        } catch (Exception e) {
            System.out.println("Error...");
            System.err.println(e.getMessage());
            System.exit(-1);
        }

        System.out.println("Moves found in AI: " + moveList.size());
        for (Move move : moveList){
            newGame.printMove(move);
        }

        if (moveList.size() == 1) {
            return moveList.get(0);
        }

        Move returnMove = new Move();
        for (int depth = 1; depth < 5; depth++) {
            moveOptions = new ArrayList<>();
            System.gc();
            System.out.println("Searching at depth " + depth);
            alphaBetaSearch(new Board(gameBoard), depth, Long.MIN_VALUE, Long.MAX_VALUE, true, true);
            if (!timeOver) {
                returnMove = new Move(pickMove(moveOptions));
            }
            if (timeOver) {
                System.out.println("Time's up...");
                System.out.println("Search completed at depth " + (depth - 1));
                break;
            }
        }
        if (returnMove.moveType == 0) {
            return null;
        }
        return returnMove;
    }

    private Move pickMove(ArrayList<MoveOption> moveOptions){
        long maxValue = Long.MIN_VALUE;
        long minValue = Long.MAX_VALUE;
        ArrayList<Move> pickedMoves = new ArrayList<>();
        for (MoveOption option : moveOptions){
            if (option.getMove() == null) {

                System.out.println("move is null...");
                System.exit(-1);
            }
            newGame.printMove(option.getMove());
            System.out.println("Move value: " + option.getValue());
            if (option.getValue() > maxValue){
                maxValue = option.getValue();
            } else if (option.getValue() < minValue){
                minValue = option.getValue();
            }
        }
        System.out.println(minValue + "" + maxValue);
        long threshold = maxValue - ((maxValue - minValue)/10);
        for (MoveOption option : moveOptions){
            if (option.getValue() >= threshold){
                pickedMoves.add(option.getMove());
            }
        }
        Move retMove = pickedMoves.get((new Random()).nextInt(pickedMoves.size()));
        System.out.print("Move picked: ");
        newGame.printMove(retMove);
        return retMove;
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
            checkSquareMoves(loc, false, 0, null, player, boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing(), moveList, boardIn);
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

    private void checkSquareMoves(CheckerLocation location, boolean continueKill, int prevDirection, Move moveIn, int playerNum, boolean isKing, ArrayList<Move> moveList, Board boardIn) {
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
                            boolean stuckInLoop = false;
                            for (CheckerLocation loc : moveIn.jumps){
                                if (move.xDestination == loc.xLocation && move.yDestination == loc.yLocation){
                                    stuckInLoop = true;
                                }
                            }
                            if ((move.xDestination == moveIn.xSource && move.yDestination == moveIn.ySource) || stuckInLoop){
                                moveOut = new Move(moveIn);
                                moveOut.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                                return;
                            }
                            moveOut = new Move(moveIn);
                        }
                        moveOut.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                        try {
                            checkSquareMoves(moveOut.jumps.get(moveOut.jumps.size() - 1), true, direc, moveOut, playerNum, isKing, moveList, boardIn);
                        } catch (StackOverflowError e) {
                            System.out.println("Found error");
                            for (int i = 0; i < 8; i++) {
                                System.out.println();
                                for (int j = 0; j < 8; j++) {
                                    System.out.print(boardIn.getBoardPieces()[i][j].getPieceColor());
                                }
                            }
                            System.out.println();
                            newGame.printMove(moveOut);
                            System.exit(-1);

                        }
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

    /*


    private void checkValidMoves(ArrayList<Move> moveList, Board boardIn, int player) {
        boolean killAvailable = false;
        ArrayList<CheckerLocation> currentCheckers = new ArrayList<>();
        if (player == BLACK) {
            currentCheckers = boardIn.getBlackPieceLocations();
        } else if (player == RED) {
            currentCheckers = boardIn.getRedPieceLocations();
        }
        for (CheckerLocation loc : currentCheckers) {
            //checkSquareMoves(loc, gameBoard, false, null, player, moveList, boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing());
            //break;
            checkSquareMoves(loc, false, 0, null, player, boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing(), moveList, boardIn);
        }
        for (Move move : moveList) {
            if (move.moveType == MOVE_KILL) {
                killAvailable = true;
            }
            System.out.println("Move: " + move.xSource + "," + move.ySource);
            newGame.printMove(move);
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

    private void checkSquareMoves(CheckerLocation location, boolean continueKill, int prevDirection, Move moveIn, int playerNum, boolean isKing, ArrayList<Move> moveList, Board boardIn) {
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
                            boolean stuckInLoop = false;
                            for (CheckerLocation loc : moveIn.jumps){
                                if (move.xDestination == loc.xLocation && move.yDestination == loc.yLocation){
                                    stuckInLoop = true;
                                }
                            }
                            if ((move.xDestination == moveIn.xSource && move.yDestination == moveIn.ySource) || stuckInLoop){
                                moveOut = new Move(moveIn);
                                moveOut.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                                return;
                            }
                            moveOut = new Move(moveIn);
                        }
                        moveOut.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                        try {
                            checkSquareMoves(moveOut.jumps.get(moveOut.jumps.size() - 1), true, direc, moveOut, playerNum, isKing, moveList, boardIn);
                        } catch (StackOverflowError e) {
                            System.out.println("Found error");
                            for (int i = 0; i < 8; i++) {
                                System.out.println();
                                for (int j = 0; j < 8; j++) {
                                    System.out.print(boardIn.getBoardPieces()[i][j].getPieceColor());
                                }
                            }
                            System.out.println();
                            newGame.printMove(moveOut);
                            System.exit(-1);

                        }
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

    private long alphaBetaSearch(Board boardIn, int depth, long alphaIn, long betaIn, boolean max, boolean isRoot) {
        if (!timeOver) {
            if ((new Date()).getTime() - startTime > timeLimit * 1000) {
                timeOver = true;
                return 0;
            }
            long alpha = alphaIn;
            long beta = betaIn;
            long branchValue;

            ArrayList<Move> moves = new ArrayList<>();
            if (max) {
                checkValidMoves(moves, boardIn, playerNum);
            } else {
                checkValidMoves(moves, boardIn, (playerNum % 2) + 1);
            }

            if (moves.size() == 0 || depth == 0) {
                return calculateHeuristic(boardIn, max);
            }
            if (max) {
                branchValue = Long.MIN_VALUE;
                for (Move move : moves) {
                    Board board = new Board(boardIn);
                    board.pieceMover(move, false);
                    long childValue = alphaBetaSearch(board, depth - 1, alpha, beta, false, false);
                    if (childValue > branchValue) {
                        branchValue = childValue;
                    }
                    if (branchValue > alpha) {
                        alpha = branchValue;
                    }
                    if (isRoot){
                        System.out.println("Adding a move...");
                        moveOptions.add(new MoveOption(move, childValue));
                    }
                    if (beta <= alpha) {
                        break;
                    }
                }
                return branchValue;
            } else {
                branchValue = Long.MAX_VALUE;
                for (Move move : moves) {
                    Board board = new Board(boardIn);
                    board.pieceMover(move, false);
                    long childValue = alphaBetaSearch(board, depth - 1, alpha, beta, true, false);
                    if (childValue < branchValue) {
                        branchValue = childValue;
                    }
                    if (branchValue < beta) {
                        beta = branchValue;
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

    private long calculateHeuristic(Board boardIn, boolean max) {
        long retVal = numPiecesValue(boardIn, playerNum) - numPiecesValue(boardIn, (playerNum % 2) + 1);
        if (!max){
            retVal = retVal;
        }
        return retVal;
    }

    private long numPiecesValue(Board boardIn, int playerNum){
        long retVal = 0;
        if (playerNum == BLACK){
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                retVal += (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) ? 5 : 3;
            }
        } else {
            for (CheckerLocation loc : boardIn.getRedPieceLocations()){
                retVal += (boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) ? 5 : 3;
            }
        }
        return retVal;
    }

    private long kingDistance (Board boardIn, int playerNum){
        long retVal = 0;
        if (playerNum == BLACK){
            for (CheckerLocation loc : boardIn.getBlackPieceLocations()){
                retVal += (7 - loc.xLocation);
            }
            retVal = retVal / boardIn.getBlackPieceLocations().size();
        }
        return retVal;
    }
}

*/