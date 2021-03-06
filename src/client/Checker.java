package client;

/**
 * Created by abhinav on 10/19/2017.
 */
public class Checker {

    private final int EMPTY = 0;
    private final int BLACK = 1;
    private final int RED = 2;

    private int pieceColor;

    int getPieceColor() {
        return pieceColor;
    }

    void setPieceColor(int pieceColor) {
        this.pieceColor = pieceColor;
    }

    private boolean isKing;

    boolean isKing() {
        return isKing;
    }

    void makeKing() {
        isKing = true;
    }

    public Checker(int color) {
        pieceColor = color;
        isKing = false;
    }

    Checker() {
        pieceColor = EMPTY;
        isKing = false;
    }

    Checker(int color, boolean king){
        pieceColor = color;
        isKing = king;
    }

    Checker(Checker checkerIn){
        this.pieceColor = checkerIn.pieceColor;
        this.isKing = checkerIn.isKing;
    }
}
