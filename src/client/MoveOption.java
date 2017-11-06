package client;

public class MoveOption {


    public Move getMove() {
        return move;
    }
    public void setMove(Move move) {
        this.move = move;
    }
    private Move move;

    public long getValue() {
        return value;
    }
    public void setValue(long value) {
        this.value = value;
    }
    private long value;

    MoveOption(Move moveIn, long valueIn){
        value = valueIn;
        move = new Move(moveIn);
    }
}
