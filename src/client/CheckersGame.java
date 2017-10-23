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

import static client.Board.boardPieces;

public class CheckersGame extends JPanel implements ActionListener, MouseListener {

    private final int EMPTY = 0;
    private final int BLACK = 1;
    private final int BLACK_KING = 2;
    private final int RED = 3;
    private final int RED_KING = 4;

    private final int NO_MOVE = 0;
    private final int MOVE_BLANK = 1;
    private final int MOVE_KILL = 2;

    private static Board gameBoard;
    private static int redScore = 12;
    private static int blackScore = 12;

    public CheckersGame() {
        gameBoard = new Board();
        setupWindow();
    }

    public void StartGame(boolean oneIsAI, boolean twoIsAI) {

    }

    void setupWindow() {
        JFrame frame = new JFrame();
        frame.setSize(720, 720);
        frame.setBackground(Color.white);
        frame.pack();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.addMouseListener(this);
        frame.requestFocus();
        frame.setVisible(true);
        frame.add(this);
    }

    void startGame(int playerOne, int playerTwo) {

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
