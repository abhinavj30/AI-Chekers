package client;

/**
 * Created by abhinav on 10/19/2017.
 */


import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;


import static client.Board.boardPieces;
import static client.Board.blackPieceLocations;
import static client.Board.redPieceLocations;

public class CheckersGame extends JPanel implements ActionListener, MouseListener {

    private final int EMPTY = 0;
    private final int BLACK = 1;
    private final int RED = 2;

    private final int NO_MOVE = 0;
    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;

    private final int MOVE_DOWN_RIGHT = 3;
    private final int MOVE_DOWN_LEFT = 13;
    private final int MOVE_UP_RIGHT = 11;
    private final int MOVE_UP_LEFT = 1;

    private static Board gameBoard;
    private static int redScore = 12;
    private static int blackScore = 12;

    private static int[] moveDirections = {1, 3, 11, 13};
    private ArrayList<MoveLocation> validMoves;

    private static JFrame frame;
    private static CheckerLocation selectedBlock;
    private static int currentPlayer;

    private static boolean postKill = false;
    private static boolean killAvailable = false;

    private static int gameStatus;

    public CheckersGame() {
        gameBoard = new Board();
        setupWindow();
        startGame(false, false);
        repaint();
        frame.setSize(800, 800);
    }

    private void startGame(boolean oneIsAI, boolean twoIsAI) {
        currentPlayer = BLACK;
        System.out.println();
        selectedBlock = new CheckerLocation(2, 2);
        checkValidMoves();
        /*
        while (redPieceLocations.size() != 0 && blackPieceLocations.size() != 0) {
            System.out.println("Color: " + boardPieces[2][0].getPieceColor());
            selectedBlock = new CheckerLocation(2, 4);
            checkValidMoves();
            return;
            //Black plays first;
            //Wait for play

        }*/
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

    private void checkValidMoves() {
        validMoves = new ArrayList<>();
        killAvailable = false;
        ArrayList<CheckerLocation> currentCheckers = new ArrayList<>();
        if (currentPlayer == BLACK){
            currentCheckers = blackPieceLocations;
        } else if (currentPlayer == RED){
            currentCheckers = redPieceLocations;
        }
        int id = 1;
        for (CheckerLocation loc : currentCheckers) {
            checkSquareMoves(loc, false, 0, null, currentPlayer, boardPieces[loc.xLocation][loc.yLocation].isKing());
            for (int direc : moveDirections) {
                MoveLocation validMove = gameBoard.checkMove(direc, loc.xLocation, loc.yLocation, currentPlayer, false, postKill, true);
                if (validMove != null) {
                    if (validMove.moveType == MOVE_KILL) {
                        killAvailable = true;
                    }
                    //validMoves.add(validMove);
                }
                id++;
            }
        }
    }

    private void checkSquareMoves(CheckerLocation location, boolean continueKill, int prevDirection, MoveLocation moveIn, int playerNum, boolean isKing) {
        boolean noMoreJumps = true;
        for (int direc : moveDirections){
            if (!continueKill || direc != getOppositeDirection(prevDirection)){
            MoveLocation move = gameBoard.checkMove(direc, location.xLocation, location.yLocation, playerNum, isKing, continueKill, true);
                if (move != null){
                    if (move.moveType == MOVE_KILL){
                        MoveLocation moveOut;
                        if (!continueKill){
                            moveOut = move;
                        } else {
                            moveOut = new MoveLocation(moveIn);
                        }
                        moveOut.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                        checkSquareMoves(new CheckerLocation(move.xDestination, move.yDestination), true, direc, moveOut, playerNum, isKing);
                        noMoreJumps = false;
                    } else {
                        move.jumps.add(new CheckerLocation(move.xDestination, move.yDestination));
                        //printMove(move);
                        validMoves.add(move);
                    }
                }
            }
        }
        if (noMoreJumps && continueKill){
            //printMove(moveIn);
            validMoves.add(moveIn);
        }
    }

    private int getOppositeDirection (int direction){
        int oppDirec = 1;
        if (direction > 10){
            oppDirec += 10;
        }
        if (direction % 10 == 1){
            oppDirec += 2;
        }
        return oppDirec;
    }

    private void makeMove(int moveType, int direction) {
        if (moveType == 0) {
            postKill = false;
            changePlayer();
        } else {
            if (gameBoard.checkMove(direction, selectedBlock.xLocation, selectedBlock.yLocation, currentPlayer, false, postKill, false).moveType == MOVE_KILL) {
                if (currentPlayer == BLACK) {
                    blackScore--;
                } else {
                    redScore--;
                }
                postKill = true;
                System.out.println("Score: Black - " + blackScore + ", Red - " + redScore);
                if (redScore * blackScore == 0) {
                    System.out.println("Game Over");
                }
            } else {
                postKill = false;
                changePlayer();
            }
        }
    }

    private void changePlayer() {
        if (currentPlayer == BLACK) {
            System.out.println("Red plays now");
            currentPlayer = RED;
        } else {
            System.out.println("Black plays now");
            currentPlayer = BLACK;
        }
    }

    private void printMove(MoveLocation move){
        System.out.print("Move: " + move.xSource + "," + move.ySource);
        for (CheckerLocation loc : move.jumps){
            System.out.print(" - " + loc.xLocation + "," + loc.yLocation);
        }
        System.out.println();
    }

    public void paint(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if ((row + col) % 2 == 0) {
                    g.setColor(Color.white);
                    g.fillRect(col * 720 / 8, row * 720 / 8, 720 / 8, 720 / 8);
                } else {
                    g.setColor(Color.gray);
                    g.fillRect(col * 720 / 8, row * 720 / 8, 720 / 8, 720 / 8);
                }
            }
        }
        if (selectedBlock.yLocation > -1 && selectedBlock.xLocation > -1) {
            g.setColor(Color.blue);
            g.fillRect(selectedBlock.yLocation * 720 / 8, selectedBlock.xLocation * 720 / 8, 720 / 8, 720 / 8);
        }
        for (MoveLocation move : validMoves) {
            if (move.xSource == selectedBlock.xLocation && move.ySource == selectedBlock.yLocation){
                printMove(move);
                for (CheckerLocation loc : move.jumps){
                    if (move.moveType == MOVE_BLANK){
                        System.out.println("Painting move...");
                        g.setColor(Color.green);
                    } else {
                        System.out.println("Printing jump...");
                        g.setColor(Color.pink);
                    }
                    g.fillRect(loc.xLocation * 720 / 8, loc.xLocation * 720 / 8, 720 / 8, 720 / 8);
                }
            }
        }
        for (CheckerLocation loc : blackPieceLocations) {
            if (boardPieces[loc.xLocation][loc.yLocation].isKing()) {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.darkGray);
            } else {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.black);
            }
        }
        for (CheckerLocation loc : redPieceLocations) {
            if (boardPieces[loc.xLocation][loc.yLocation].isKing()) {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.magenta);
            } else {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.red);
            }
        }
    }

    private void updateBoard() {
        checkValidMoves();
        boolean moreKills = false;
        if (postKill) {
            for (MoveLocation loc : validMoves) {
                if (loc.moveType == MOVE_KILL) {
                    moreKills = true;
                }
            }
            if (!moreKills) {
                postKill = false;
                changePlayer();
            }

        }
        repaint();
    }

    private void drawChecker(int iLoc, int jLoc, Graphics g, Color color) {
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setColor(color);
        g.fillOval((iLoc * 720 / 8) + 2, (jLoc * 720 / 8) + 2, 720 / 8 - 4, 720 / 8 - 4);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        int selectedCol = (e.getX() - 8) / 90;
        int selectedRow = (e.getY() - 30) / 90;
        System.out.println("Clicked: " + selectedRow + ", " + selectedCol);
        for (MoveLocation loc : validMoves) {
            if (loc.jumps.get(loc.jumps.size() - 1).xLocation == selectedRow && loc.jumps.get(loc.jumps.size() - 1).yLocation == selectedCol && loc.xSource == selectedBlock.xLocation && loc.ySource == selectedBlock.yLocation) {
                if (loc.moveType != MOVE_BLANK || !killAvailable) {
                    int direction;
                    if (selectedBlock.xLocation - selectedRow > 0) {
                        if (selectedBlock.yLocation - selectedCol > 0) {
                            direction = MOVE_UP_LEFT;
                        } else {
                            direction = MOVE_UP_RIGHT;
                        }
                    } else {
                        if (selectedBlock.yLocation - selectedCol > 0) {
                            direction = MOVE_DOWN_LEFT;
                        } else {
                            direction = MOVE_DOWN_RIGHT;
                        }
                    }
                    makeMove(1, direction);
                    selectedBlock = new CheckerLocation(selectedRow, selectedCol);
                    updateBoard();
                    return;
                }
            }
        }
        if (boardPieces[selectedRow][selectedCol].getPieceColor() == currentPlayer && !postKill) {
            selectedBlock = new CheckerLocation(selectedRow, selectedCol);
        }
        updateBoard();
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
