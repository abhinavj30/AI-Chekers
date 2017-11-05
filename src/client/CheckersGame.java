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

    public static Board gameBoard;
    private static int redScore = 12;
    private static int blackScore = 12;

    private final int[] moveDirections = {1, 3, 11, 13};
    private static ArrayList<MoveLocation> validMoves;

    private static JFrame frame;
    private static CheckerLocation selectedBlock;
    private static int currentPlayer;

    private static boolean postKill = false;
    private static boolean killAvailable = false;

    private static int gameStatus;

    CheckersGame() {
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
        validMoves = new ArrayList<>();
        checkValidMoves(validMoves, gameBoard, currentPlayer);
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

    public void checkValidMoves(ArrayList<MoveLocation> moveList, Board boardIn, int player) {
        killAvailable = false;
        ArrayList<CheckerLocation> currentCheckers = new ArrayList<>();
        if (player == BLACK) {
            currentCheckers = boardIn.blackPieceLocations;
        } else if (player == RED) {
            currentCheckers = boardIn.redPieceLocations;
        }
        for (CheckerLocation loc : currentCheckers) {
            checkSquareMoves(loc, false, 0, null, player, boardIn.boardPieces[loc.xLocation][loc.yLocation].isKing(), moveList, boardIn);
        }
        if (killAvailable) {
            System.out.println("Kill available...");
            Iterator<MoveLocation> iter = moveList.iterator();
            while (iter.hasNext()) {
                MoveLocation move = iter.next();
                if (move.moveType == MOVE_BLANK) {
                    iter.remove();
                }
            }
        }
    }

    private void checkSquareMoves(CheckerLocation location, boolean continueKill, int prevDirection, MoveLocation moveIn, int playerNum, boolean isKing, ArrayList<MoveLocation> moveList, Board boardIn) {
        boolean noMoreJumps = true;
        for (int direc : moveDirections) {
            if (!continueKill || direc != getOppositeDirection(prevDirection)) {
                MoveLocation move = boardIn.checkMove(direc, location.xLocation, location.yLocation, playerNum, isKing, continueKill);
                if (move != null) {
                    if (move.moveType == MOVE_KILL) {
                        killAvailable = true;
                        MoveLocation moveOut;
                        if (!continueKill) {
                            moveOut = move;
                        } else {
                            moveOut = new MoveLocation(moveIn);
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

    public void makeMove(MoveLocation move) {
        if (move.moveType == MOVE_KILL) {
            if (currentPlayer == BLACK) {
                blackScore--;
            } else {
                redScore--;
            }
            System.out.println("Score: Black - " + blackScore + ", Red - " + redScore);
            if (redScore * blackScore == 0) {
                System.out.println("Game Over");
            }
        }
        gameBoard.pieceMover(move);
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

    private void printMove(MoveLocation move) {
        System.out.print("Move: " + move.xSource + "," + move.ySource);
        for (CheckerLocation loc : move.jumps) {
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
            if (move.xSource == selectedBlock.xLocation && move.ySource == selectedBlock.yLocation) {
                for (CheckerLocation loc : move.jumps) {
                    if (move.moveType == MOVE_BLANK) {
                        g.setColor(Color.green);
                    } else {
                        g.setColor(Color.pink);
                    }
                    g.fillRect(loc.yLocation * 720 / 8, loc.xLocation * 720 / 8, 720 / 8, 720 / 8);
                }
            }
        }
        for (CheckerLocation loc : gameBoard.blackPieceLocations) {
            if (gameBoard.boardPieces[loc.xLocation][loc.yLocation].isKing()) {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.darkGray);
            } else {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.black);
            }
        }
        for (CheckerLocation loc : gameBoard.redPieceLocations) {
            if (gameBoard.boardPieces[loc.xLocation][loc.yLocation].isKing()) {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.magenta);
            } else {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.red);
            }
        }
    }

    private void updateBoard() {
        validMoves = new ArrayList<>();
        checkValidMoves(validMoves, gameBoard, currentPlayer);
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
            CheckerLocation dest = loc.jumps.get(loc.jumps.size() - 1);
            if (dest.xLocation == selectedRow && dest.yLocation == selectedCol && loc.xSource == selectedBlock.xLocation && loc.ySource == selectedBlock.yLocation) {
                makeMove(loc);
                selectedBlock = new CheckerLocation(selectedRow, selectedCol);
                updateBoard();
                return;
            }

        }
        if (gameBoard.boardPieces[selectedRow][selectedCol].getPieceColor() == currentPlayer && !postKill) {
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
