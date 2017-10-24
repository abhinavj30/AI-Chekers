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

import client.Board.*;
import oracle.jrockit.jfr.JFR;

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
    private final int MOVE_UP_RIGHT = 1;
    private final int MOVE_UP_LEFT = 11;

    private static Board gameBoard;
    private static int redScore = 13;
    private static int blackScore = 13;

    private static int[] moveDirections = {1, 3, 11, 13};
    private ArrayList<MoveLocation> validMoves;

    private static JFrame frame;
    private static CheckerLocation selectedBlock;
    private static int currentPlayer;

    private static int gameStatus;

    public CheckersGame() {
        gameBoard = new Board();
        setupWindow();
        startGame(false, false);
        repaint();
        frame.setSize(800, 800);
    }

    public void startGame(boolean oneIsAI, boolean twoIsAI) {
        currentPlayer = BLACK;
        System.out.println();
        while (redPieceLocations.size() != 0 && blackPieceLocations.size() != 0) {
            System.out.println("Color: " + boardPieces[2][0].getPieceColor());
            selectedBlock = new CheckerLocation(2, 4);
            checkValidMoves();
            return;
            //Black plays first;
            //Wait for play

        }
    }

    void setupWindow() {
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
        for (int direc : moveDirections) {
            validMoves.add(gameBoard.moveChecker(direc, selectedBlock.xLocation, selectedBlock.yLocation, currentPlayer, false, true));
        }
    }

    private void makeMove(int moveType, int direction) {
        if (gameBoard.moveChecker(direction, selectedBlock.xLocation, selectedBlock.yLocation, currentPlayer, false, false).moveType == MOVE_KILL) {
            if (currentPlayer == BLACK) {
                blackScore--;
            } else {
                redScore--;
            }
        }
        System.out.println("Score: Black - " + blackScore + ", Red - " + redScore);
        if (redScore * blackScore == 0){
            System.out.println("Game Over");
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
            g.setColor(Color.blue);
            g.fillRect(selectedBlock.yLocation * 720 / 8, selectedBlock.xLocation * 720 / 8, 720 / 8, 720 / 8);
            for (MoveLocation loc : validMoves) {
                if (loc.moveType == MOVE_BLANK) {
                    g.setColor(Color.green);
                    g.fillRect(loc.yLocation * 720 / 8, loc.xLocation * 720 / 8, 720 / 8, 720 / 8);
                } else if (loc.moveType == MOVE_KILL) {
                    g.setColor(Color.pink);
                    g.fillRect(loc.yLocation * 720 / 8, loc.xLocation * 720 / 8, 720 / 8, 720 / 8);
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
    }

    private void updateBoard() {
        checkValidMoves();
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
            if (loc.xLocation == selectedRow && loc.yLocation == selectedCol) {
                int direction;
                if (selectedBlock.xLocation - selectedRow > 0) {
                    if (selectedBlock.yLocation - selectedCol > 0) {
                        direction = MOVE_UP_RIGHT;
                    } else {
                        direction = MOVE_UP_LEFT;
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
            }
        }
        if (boardPieces[selectedRow][selectedCol].getPieceColor() == currentPlayer){
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
