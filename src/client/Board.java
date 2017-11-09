package client;

import java.util.ArrayList;


import static client.CheckersGame.gameBoard;
import static client.RunGame.newGame;

/**
 * Created by abhinav on 10/19/2017.
 * Basic board class
 */
public class Board {


    public Checker[][] getBoardPieces() {
        return boardPieces;
    }
    public void setBoardPieces(Checker[][] boardPieces) {
        this.boardPieces = boardPieces;
    }
    private Checker[][] boardPieces;


    public ArrayList<CheckerLocation> getBlackPieceLocations() {
        return blackPieceLocations;
    }
    public void setBlackPieceLocations(ArrayList<CheckerLocation> blackPieceLocations) {
        this.blackPieceLocations = blackPieceLocations;
    }
    private ArrayList<CheckerLocation> blackPieceLocations;

    public ArrayList<CheckerLocation> getRedPieceLocations() {
        return redPieceLocations;
    }
    public void setRedPieceLocations(ArrayList<CheckerLocation> redPieceLocations) {
        this.redPieceLocations = redPieceLocations;
    }
    private ArrayList<CheckerLocation> redPieceLocations;

    private final int EMPTY = 0;
    private final int BLACK = 1;
    private final int RED = 2;

    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;

    private final int MOVE_DOWN_RIGHT = 3;
    private final int MOVE_DOWN_LEFT = 13;
    private final int MOVE_UP_RIGHT = 11;
    private final int MOVE_UP_LEFT = 1;

    private final int[] moveDirections = {1, 3, 11, 13};

    Board() {
        boardPieces = new Checker[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                boardPieces[i][j] = new Checker();
            }
        }
        //this.initBoard3();
        this.initializeBoard();

        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                int kingVal = (boardPieces[i][j].isKing()) ? 2 : 0;
                System.out.print((boardPieces[i][j].getPieceColor() + kingVal) + " ");
            }
        }
    }

    public Board(Board inBoard) {
        boardPieces = new Checker[8][8];
        this.blackPieceLocations = new ArrayList<>();
        this.redPieceLocations = new ArrayList<>();

        for (int i = 0; i < 8; i++){
            for (int j =0; j < 8; j++){
                this.boardPieces[i][j] = new Checker(inBoard.boardPieces[i][j]);
            }
        }

        for (CheckerLocation loc :inBoard.blackPieceLocations){
            this.blackPieceLocations.add(new CheckerLocation(loc));
        }
        for (CheckerLocation loc : inBoard.redPieceLocations) {
            this.redPieceLocations.add(new CheckerLocation(loc));
        }
    }

    private void initializeBoard() {

        blackPieceLocations = new ArrayList<>();
        redPieceLocations = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                boardPieces[i][j].setPieceColor(BLACK);
                blackPieceLocations.add(new CheckerLocation(i, j));
            }
        }
        for (int i = 5; i < 8; i++) {
            for (int j = (i + 1) % 2; j < 8; j += 2) {
                boardPieces[i][j].setPieceColor(RED);
                redPieceLocations.add(new CheckerLocation(i, j));
            }
        }
    }

    private void initBoard2() {
        blackPieceLocations = new ArrayList<>();
        redPieceLocations = new ArrayList<>();

        boardPieces[1][4].setPieceColor(RED);
        redPieceLocations.add(new CheckerLocation(1, 4));
        boardPieces[1][6].setPieceColor(RED);
        redPieceLocations.add(new CheckerLocation(1, 6));
        boardPieces[3][4].setPieceColor(RED);
        redPieceLocations.add(new CheckerLocation(3, 4));
        boardPieces[3][6].setPieceColor(RED);
        redPieceLocations.add(new CheckerLocation(3, 6));

        boardPieces[4][5].setPieceColor(BLACK);
        boardPieces[4][5].makeKing();
        blackPieceLocations.add(new CheckerLocation(4, 5));
    }

    private void initBoard3() {
        blackPieceLocations = new ArrayList<>();
        redPieceLocations = new ArrayList<>();

        boardPieces[0][1].setPieceColor(RED);
        boardPieces[0][1].makeKing();
        redPieceLocations.add(new CheckerLocation(0, 1));

        boardPieces[5][6].setPieceColor(BLACK);
        boardPieces[5][6].makeKing();
        blackPieceLocations.add(new CheckerLocation(5, 6));

        boardPieces[7][2].setPieceColor(BLACK);
        boardPieces[7][2].makeKing();
        blackPieceLocations.add(new CheckerLocation(7, 2));
    }

    public Move checkMove(int direction, int iOrig, int jOrig, int playerNum, boolean king, boolean postKill) {
        if (king || playerNum == ((direction % 10) % 3) + 1) {
            int[] destCoords = {iOrig + (direction % 5) - 2, jOrig + (direction % 4) - 2};
            for (int coord : destCoords) {
                if (coord < 0 || coord > 7) {
                    return null;
                }
            }
            if (boardPieces[destCoords[0]][destCoords[1]].getPieceColor() == EMPTY) {
                if (!postKill) {
                    return new Move(iOrig, jOrig, destCoords[0], destCoords[1], MOVE_BLANK);
                }
            } else if (boardPieces[destCoords[0]][destCoords[1]].getPieceColor() == playerNum) {
                return null;
            } else {
                int[] killCoords = new int[2];
                killCoords[0] = iOrig + ((direction % 5) - 2) * 2;
                killCoords[1] = jOrig + ((direction % 4) - 2) * 2;
                for (int coord : killCoords) {
                    if (coord < 0 || coord > 7) {
                        return null;
                    }
                }
                if (boardPieces[killCoords[0]][killCoords[1]].getPieceColor() == EMPTY) {
                    return new Move(iOrig, jOrig, killCoords[0], killCoords[1], MOVE_KILL);
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public void pieceMover(Move moveLoc, boolean noKing) {
        if (moveLoc.moveType == MOVE_BLANK) {
            this.movePiece(moveLoc.xSource, moveLoc.ySource, moveLoc.xDestination, moveLoc.yDestination, false);
        } else {
            CheckerLocation dest;
            CheckerLocation source;
            for (int i = 0; i < moveLoc.jumps.size(); i++) {
                dest = moveLoc.jumps.get(i);
                if (i == 0) {
                    source = new CheckerLocation(moveLoc.xSource, moveLoc.ySource);
                } else {
                    source = moveLoc.jumps.get(i - 1);
                }
                this.movePiece(source.xLocation, source.yLocation, dest.xLocation, dest.yLocation, noKing);
                this.removeChecker((source.xLocation + dest.xLocation) / 2, (source.yLocation + dest.yLocation) / 2);
            }
        }
    }

    private void removeChecker(int iLoc, int jLoc) {
        if (boardPieces[iLoc][jLoc].getPieceColor() == BLACK) {
            blackPieceLocations.remove(new CheckerLocation(iLoc, jLoc));
        } else {
            redPieceLocations.remove(new CheckerLocation(iLoc, jLoc));
        }
        boardPieces[iLoc][jLoc] = new Checker();
    }

    private void movePiece(int iOrig, int jOrig, int iDest, int jDest, boolean noKing) {
        boardPieces[iDest][jDest] = new Checker(boardPieces[iOrig][jOrig].getPieceColor(), boardPieces[iOrig][jOrig].isKing());
        if (boardPieces[iOrig][jOrig].getPieceColor() == BLACK) {
            blackPieceLocations.remove(new CheckerLocation(iOrig, jOrig));
            blackPieceLocations.add(new CheckerLocation(iDest, jDest));
        } else {
            redPieceLocations.remove(new CheckerLocation(iOrig, jOrig));
            redPieceLocations.add(new CheckerLocation(iDest, jDest));
        }
        if (!noKing && (iDest == 0 || iDest == 7)) {
            boardPieces[iDest][jDest].makeKing();
        }
        boardPieces[iOrig][jOrig] = new Checker();
    }
}