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

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

    public Checker(int color) {
        pieceColor = color;
        isKing = false;
    }

    Checker() {
        pieceColor = EMPTY;
        isKing = false;
    }
}
