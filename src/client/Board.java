package client;

import java.util.ArrayList;


import static client.CheckersGame.gameBoard;
import static client.RunGame.newGame;

/**
 * Created by abhinav on 10/19/2017.
 * Basic board class
 */
public class Board {


    public Checker[][] boardPieces;
    public ArrayList<CheckerLocation> blackPieceLocations;
    public ArrayList<CheckerLocation> redPieceLocations;

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
        this.initializeBoard();

        for (int i = 0; i < 8; i++) {
            System.out.println();
            for (int j = 0; j < 8; j++) {
                System.out.print(boardPieces[i][j].getPieceColor());
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

    public void pieceMover(Move moveLoc) {
        if (moveLoc.moveType == MOVE_BLANK) {
            this.movePiece(moveLoc.xSource, moveLoc.ySource, moveLoc.xDestination, moveLoc.yDestination);
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
                this.movePiece(source.xLocation, source.yLocation, dest.xLocation, dest.yLocation);
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

    private void movePiece(int iOrig, int jOrig, int iDest, int jDest) {
        boardPieces[iDest][jDest] = new Checker(boardPieces[iOrig][jOrig].getPieceColor(), boardPieces[iOrig][jOrig].isKing());
        if (boardPieces[iOrig][jOrig].getPieceColor() == BLACK) {
            blackPieceLocations.remove(new CheckerLocation(iOrig, jOrig));
            blackPieceLocations.add(new CheckerLocation(iDest, jDest));
        } else {
            redPieceLocations.remove(new CheckerLocation(iOrig, jOrig));
            redPieceLocations.add(new CheckerLocation(iDest, jDest));
        }
        if (iDest == 0 || iDest == 7) {
            boardPieces[iDest][jDest].makeKing();
        }
        boardPieces[iOrig][jOrig] = new Checker();
    }
}