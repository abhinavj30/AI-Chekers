package client;


/**
 * Created by abhinav on 10/19/2017.
 * Basic board class
 */
class Board {


    static Checker[][] boardPieces;

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


        for (int i = 0; i < 3; i++) {
            for (int j = i % 2; j < 8; j += 2) {
                boardPieces[i][j].setPieceColor(BLACK);
            }
        }
        for (int i = 5; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                boardPieces[i][j].setPieceColor(RED);
            }
        }
    }


    private int moveChecker(int direction, int iOrig, int jOrig, int playerNum, boolean postKill, boolean justCheck) {
        if (boardPieces[iOrig][jOrig].getPieceColor() == playerNum) {
            if (boardPieces[iOrig][jOrig].isKing() || boardPieces[iOrig][jOrig].getPieceColor() == ((direction % 10) % 3) + 1) {
                int[] destCoords = {iOrig + (direction % 5) - 2, jOrig + (direction % 4) - 2};
                for (int coord : destCoords) {
                    if (coord < 1 || coord > 7) {
                        return NO_MOVE;
                    }
                }
                if (!postKill && boardPieces[destCoords[0]][destCoords[1]].getPieceColor() == EMPTY) {
                    if (!justCheck) {
                        boardPieces[destCoords[0]][destCoords[1]].setPieceColor(boardPieces[iOrig][jOrig].getPieceColor());
                        boardPieces[destCoords[0]][destCoords[1]].setKing(boardPieces[iOrig][jOrig].isKing());
                        boardPieces[iOrig][jOrig] = new Checker();
                    }
                    return MOVE_BLANK;
                } else if (boardPieces[destCoords[0]][destCoords[1]].getPieceColor() == playerNum) {
                    return NO_MOVE;
                } else {
                    int[] killCoords = new int[2];
                    killCoords[0] = destCoords[0] * 2;
                    killCoords[1] = destCoords[1] * 2;
                    for (int coord : killCoords) {
                        if (coord < 1 || coord > 7) {
                            return NO_MOVE;
                        }
                    }
                    if (boardPieces[killCoords[0]][killCoords[1]].getPieceColor() == EMPTY) {
                        if (!justCheck) {
                            boardPieces[killCoords[0]][killCoords[1]].setPieceColor(boardPieces[iOrig][jOrig].getPieceColor());
                            boardPieces[killCoords[0]][killCoords[1]].setKing(boardPieces[iOrig][jOrig].isKing());
                            boardPieces[destCoords[0]][destCoords[1]] = new Checker();
                            boardPieces[iOrig][jOrig] = new Checker();
                        }
                        return MOVE_KILL;
                    } else {
                        return NO_MOVE;
                    }
                }
            }
        }
        return NO_MOVE;
    }

}