package client;


import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Created by abhinav on 10/19/2017.
 * Basic board class
 */
class Board {


    static Checker[][] boardPieces;
    static ArrayList<CheckerLocation> blackPieceLocations;
    static ArrayList<CheckerLocation> redPieceLocations;

    private final int EMPTY = 0;
    private final int BLACK = 1;
    private final int RED = 2;

    private final int NO_MOVE = 0;
    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;

    private final int MOVE_DOWN_RIGHT = 3;
    private final int MOVE_DOWN_LEFT = 13;
    private final int MOVE_UP_RIGHT = 1;
    private final int MOVE_UP_LEFT = 11;

    Board() {
        boardPieces = new Checker[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardPieces[i][j] = new Checker();
            }
        }
        this.initializeBoard();

        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                System.out.print(boardPieces[i][j].getPieceColor());
            }
        }
    }

    private void initializeBoard() {

        blackPieceLocations = new ArrayList<>();
        redPieceLocations = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = i % 2; j < 8; j += 2) {
                boardPieces[i][j].setPieceColor(BLACK);
                blackPieceLocations.add(new CheckerLocation(i, j));
            }
        }
        for (int i = 5; i < 8; i++) {
            for (int j = i % 2; j < 8; j += 2) {
                boardPieces[i][j].setPieceColor(RED);
                redPieceLocations.add(new CheckerLocation(i, j));
            }
        }
    }

    MoveLocation moveChecker(int direction, int iOrig, int jOrig, int playerNum, boolean postKill, boolean justCheck) {
        if (boardPieces[iOrig][jOrig].getPieceColor() == playerNum) {
            if (boardPieces[iOrig][jOrig].isKing() || boardPieces[iOrig][jOrig].getPieceColor() == ((direction % 10) % 3) + 1) {
                int[] destCoords = {iOrig + (direction % 5) - 2, jOrig + (direction % 4) - 2};
                for (int coord : destCoords) {
                    if (coord < 0 || coord > 7) {
                        return new MoveLocation(-1, -1, NO_MOVE);
                    }
                }
                if (!postKill && boardPieces[destCoords[0]][destCoords[1]].getPieceColor() == EMPTY) {
                    if (!justCheck) {
                        moveLocation(iOrig, jOrig, destCoords[0], destCoords[1]);
                    }
                    return new MoveLocation(destCoords[0], destCoords[1], MOVE_BLANK);
                } else if (boardPieces[destCoords[0]][destCoords[1]].getPieceColor() == playerNum) {
                    return new MoveLocation(-1, -1, NO_MOVE);
                } else {
                    int[] killCoords = new int[2];
                    killCoords[0] = iOrig + ((direction % 5) - 2) * 2;
                    killCoords[1] = jOrig + ((direction % 4) - 2) * 2;
                    for (int coord : killCoords) {
                        if (coord < 0 || coord > 7) {
                            return new MoveLocation(-1, -1, NO_MOVE);
                        }
                    }
                    if (boardPieces[killCoords[0]][killCoords[1]].getPieceColor() == EMPTY) {
                        if (!justCheck) {
                            moveLocation(iOrig, jOrig, killCoords[0], killCoords[1]);
                            removeChecker(destCoords[0], destCoords[1]);
                        }
                        return new MoveLocation(killCoords[0], killCoords[1], MOVE_KILL);
                    } else {
                        return new MoveLocation(-1, -1, NO_MOVE);
                    }
                }
            }
        }
        return new MoveLocation(0, 0, NO_MOVE);
    }

    private void removeChecker(int iLoc, int jLoc) {
        if (boardPieces[iLoc][jLoc].getPieceColor() == BLACK) {
            blackPieceLocations.remove(new CheckerLocation(iLoc, jLoc));
        } else {
            redPieceLocations.remove(new CheckerLocation(iLoc, jLoc));
        }
        boardPieces[iLoc][jLoc] = new Checker();
    }

    private void moveLocation(int iOrig, int jOrig, int iDest, int jDest) {
        boardPieces[iDest][jDest] = new Checker(boardPieces[iOrig][jOrig].getPieceColor(), boardPieces[iOrig][jOrig].isKing());
        if (boardPieces[iOrig][jOrig].getPieceColor() == BLACK) {
            blackPieceLocations.remove(new CheckerLocation(iOrig, jOrig));
            blackPieceLocations.add(new CheckerLocation(iDest, jDest));
        } else {
            redPieceLocations.remove(new CheckerLocation(iOrig, jOrig));
            redPieceLocations.add(new CheckerLocation(iDest, jDest));
        }
        if (iDest == 0 || iDest == 7){
            boardPieces[iDest][jDest].setKing(true);
        }
        boardPieces[iOrig][jOrig] = new Checker();
    }
}