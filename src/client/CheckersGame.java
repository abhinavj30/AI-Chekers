package client;

/**
 * Created by abhinav on 10/19/2017.
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;

public class CheckersGame extends JPanel implements ActionListener, MouseListener {

    private final int BLACK = 1;
    private final int RED = 2;

    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;

    private final int MOVE_DOWN_RIGHT = 3;
    private final int MOVE_DOWN_LEFT = 13;
    private final int MOVE_UP_RIGHT = 11;
    private final int MOVE_UP_LEFT = 1;

    static Board gameBoard;
    private static int redScore = 12;
    private static int blackScore = 12;

    private final int[] moveDirections = {1, 3, 11, 13};
    private static ArrayList<Move> validMoves;

    private static JFrame frame;
    private static CheckerLocation selectedBlock;
    private static ArrayList<Move> selectedMoves;
    private static Move selectedMove;
    private static int currentPlayer;

    private final boolean blackIsAI;
    private final boolean redIsAI;
    private final int aiTime;

    private AI blackAI;
    private AI redAI;

    CheckersGame(boolean oneIsAI, boolean twoIsAI, int aiTime) {
        blackIsAI = oneIsAI;
        redIsAI = twoIsAI;
        this.aiTime = aiTime;

        gameBoard = new Board();
        selectedMoves = new ArrayList<>();
        setupWindow();
        startGame();
        frame.setSize(800, 800);
    }

    private void startGame() {
        repaint();

        if (blackIsAI) {
            blackAI = new AI(BLACK, aiTime);
        }
        if (redIsAI){
            redAI = new AI(RED, aiTime);
        }
        currentPlayer = BLACK;

        System.out.println();
        selectedBlock = new CheckerLocation(-1, -1);

        updateBoard();
        printValidMoves();

        if (blackIsAI){
            makeMove(blackAI.aiMove());
        }
    }

    private void setupWindow() {
        frame = new JFrame();
        frame.setSize(800, 800);
        frame.setBackground(Color.white);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.addMouseListener(this);
        frame.requestFocus();
        frame.setVisible(true);
        frame.add(this);
    }

    void checkValidMoves(ArrayList<Move> moveList, Board boardIn, int player) {
        System.out.println("Checking valid moves...");
        boolean killAvailable = false;
        ArrayList<CheckerLocation> currentCheckers = new ArrayList<>();
        if (player == BLACK) {
            currentCheckers = boardIn.getBlackPieceLocations();
        } else if (player == RED) {
            currentCheckers = boardIn.getRedPieceLocations();
        }
        for (CheckerLocation loc : currentCheckers) {
            checkSquareMoves(loc, gameBoard, false, null, player, moveList, boardIn.getBoardPieces()[loc.xLocation][loc.yLocation].isKing());
        }
        for (Move move : moveList) {
            if (move.moveType == MOVE_KILL){
                killAvailable = true;
            }
        }
        if (killAvailable) {
            System.out.println("Kill available...");
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
                    moveList.add(move);
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
            moveList.add(moveIn);
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
                            moveOut = new Move(move);
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

    private void makeMove(Move move) {
        gameBoard.pieceMover(move, false);
        blackScore = gameBoard.getBlackPieceLocations().size();
        redScore = gameBoard.getRedPieceLocations().size();
        System.out.println("Score: Black - " + blackScore + ", Red - " + redScore);
        if (redScore * blackScore == 0) {
            System.out.println("Game Over");
            JOptionPane.showMessageDialog(null, "Game Over", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        changePlayer();
    }

    private void changePlayer() {
        if (currentPlayer == BLACK) {
            System.out.println("Red plays now");
            currentPlayer = RED;
        } else {
            System.out.println("Black plays now");
            currentPlayer = BLACK;
        }
        updateBoard();
        printValidMoves();
        if (currentPlayer == BLACK && blackIsAI){
            makeMove(blackAI.aiMove());
        }
        if (currentPlayer == RED && redIsAI) {
            makeMove(redAI.aiMove());
        }
        System.out.println("current player: " + currentPlayer);
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

    private void printValidMoves() {
        System.out.println("Valid moves for current player " + currentPlayer);
        for (Move move : validMoves){
            printMove(move);
        }
    }

    public void paint(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(Color.gray);
                    g.fillRect(col * 720 / 8, row * 720 / 8, 720 / 8, 720 / 8);
                } else {
                    g.setColor(Color.white);
                    g.fillRect(col * 720 / 8, row * 720 / 8, 720 / 8, 720 / 8);
                }
            }
        }
        if (selectedBlock.yLocation > -1 && selectedBlock.xLocation > -1) {
            g.setColor(Color.blue);
            g.fillRect(selectedBlock.yLocation * 720 / 8, selectedBlock.xLocation * 720 / 8, 720 / 8, 720 / 8);
        }
        if (selectedMoves.size() != 0) {
            for (Move move : selectedMoves) {
                for (CheckerLocation loc : move.jumps) {
                    if (move.moveType == MOVE_BLANK) {
                        g.setColor(Color.green);
                    } else {
                        g.setColor(Color.pink);
                        if (move.jumps.indexOf(loc) == move.jumps.size() - 1) {
                            g.setColor(Color.red);
                        }
                    }
                    g.fillRect(loc.yLocation * 720 / 8, loc.xLocation * 720 / 8, 720 / 8, 720 / 8);
                }
            }
        }
        for (CheckerLocation loc : gameBoard.getBlackPieceLocations()) {
            if (gameBoard.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.darkGray);
            } else {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.black);
            }
        }
        for (CheckerLocation loc : gameBoard.getRedPieceLocations()) {
            if (gameBoard.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.magenta);
            } else {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.red);
            }
        }
    }

    private void updateBoard() {
        selectedMoves = new ArrayList<>();
        selectedBlock = new CheckerLocation(-1, -1);
        validMoves = new ArrayList<>();
        checkValidMoves(validMoves, gameBoard, currentPlayer);
        repaint();
    }

    private void drawChecker(int iLoc, int jLoc, Graphics g, Color color) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(color);
        g.fillOval((iLoc * 720 / 8) + 2, (jLoc * 720 / 8) + 2, 720 / 8 - 4, 720 / 8 - 4);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        CheckerLocation clickedLocation = new CheckerLocation((e.getY() - 30) / 90, (e.getX() - 8) / 90);
        if ((currentPlayer == BLACK && !blackIsAI) || (currentPlayer == RED && !redIsAI)) {
            for (Move move : selectedMoves) {
                CheckerLocation dest = move.jumps.get(move.jumps.size() - 1);
                if (dest.equals(clickedLocation) && move.xSource == selectedBlock.xLocation && move.ySource == selectedBlock.yLocation) {
                    if (checkSameInitEnd()){
                        System.out.println("Multiple moves terminated here. Please select a move by selecting a route block.");
                    } else {
                        makeMove(move);
                        updateBoard();
                        return;
                    }
                }
                if (move.jumps.contains(clickedLocation) && checkSameInitEnd()){
                    if (checkSameRoute(clickedLocation)){
                        System.out.println("This location is in multiple moves. Please select a different move.");
                    } else {
                        makeMove(move);
                        updateBoard();
                        return;
                    }
                }
            }
            if (gameBoard.getBoardPieces()[clickedLocation.xLocation][clickedLocation.yLocation].getPieceColor() == currentPlayer) {
                selectedBlock = new CheckerLocation(clickedLocation);
                selectedMoves = new ArrayList<>();
                for (Move move: validMoves){
                    if (move.xSource == selectedBlock.xLocation && move.ySource == selectedBlock.yLocation){
                        selectedMoves.add(move);
                    }
                }
            }
            repaint();
        }
    }

    private boolean checkSameInitEnd(){
        for (Move move : selectedMoves){
            for (Move move1 : selectedMoves){
                if (!move.equals(move1)) {
                    if (move.moveType == MOVE_KILL) {
                        if (move.jumps.get(move.jumps.size() - 1).equals(move1.jumps.get(move.jumps.size() - 1))) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean checkSameRoute(CheckerLocation location){
        boolean retVal = true;
        if (checkSameInitEnd()){
            for (Move move : selectedMoves){
                for (Move move1 : selectedMoves){
                    if (!move.equals(move1)) {
                        if (move.moveType == MOVE_KILL) {
                            if (move.jumps.get(move.jumps.size() - 1).equals(move1.jumps.get(move.jumps.size() - 1))) {
                                if ((move.jumps.contains(location) && !move1.jumps.contains(location)) || (!move.jumps.contains(location) && move1.jumps.contains(location))) {
                                    retVal = false;
                                } else if (move.jumps.contains(location) && move1.jumps.contains(location)){
                                    retVal = true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return retVal;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
