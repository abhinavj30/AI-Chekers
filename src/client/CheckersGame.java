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

    private static Board gameBoard;
    private static int redScore = 12;
    private static int blackScore = 12;

    private static int[] moveDirections = {1, 3, 11, 13};
    private ArrayList<MoveLocation> validMoves;

    private static JFrame frame;

    public CheckersGame() {
        gameBoard = new Board();
        setupWindow();
        startGame(false, false);
        repaint();
        frame.setSize(800, 800);
    }

    public void startGame(boolean oneIsAI, boolean twoIsAI) {
        int playerNum = BLACK;
        System.out.println();
        while (redPieceLocations.size() != 0 && blackPieceLocations.size() != 0) {
            System.out.println("Color: " + boardPieces[2][0].getPieceColor());
            checkValidMoves(2, 2, playerNum);
            System.out.println("Test: " + validMoves);
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

    void checkValidMoves(int iOrig, int jOrig, int playerNum) {
        validMoves = new ArrayList<>();
        for (int direc : moveDirections) {
            validMoves.add(gameBoard.moveChecker(direc, iOrig, jOrig, playerNum, false, true));
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
            for (MoveLocation loc : validMoves) {
                if (loc.moveType == MOVE_BLANK) {
                    g.setColor(Color.green);
                    g.fillRect(loc.yLocation * 720 / 8, loc.xLocation * 720 / 8, 720 / 8, 720 / 8);
                }
                else if (loc.moveType == MOVE_KILL){
                    g.setColor(Color.pink);
                    g.fillRect(loc.yLocation * 720 / 8, loc.xLocation * 720 / 8, 720 / 8, 720 / 8);
                }
            }
        }
        System.out.println("Painting...");
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

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
