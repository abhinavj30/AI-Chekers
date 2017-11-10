package client;

/**
 * Created by abhinav on 10/19/2017.
 */

import com.sun.corba.se.spi.ior.MakeImmutable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

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
    private static int currentPlayer;

    private ArrayList<CheckerLocation> killDestinations;

    private final boolean blackIsAI;
    private final boolean redIsAI;
    private final int aiTime;

    private boolean killAvailable;

    private String[] playerNames = {"None", "Black", "Red"};

    private AI blackAI;
    private AI redAI;

    CheckersGame(boolean oneIsAI, boolean twoIsAI, int aiTime, Board boardLoaded, int currentPlayerLoaded) {
        blackIsAI = oneIsAI;
        redIsAI = twoIsAI;
        this.aiTime = aiTime;

        if (boardLoaded != null) {
            gameBoard = new Board(boardLoaded);
        } else {
            gameBoard = new Board();
        }
        currentPlayer = currentPlayerLoaded;
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

        System.out.println();
        selectedBlock = new CheckerLocation(-1, -1);

        updateBoard();
        printValidMoves();

        if (currentPlayer == RED && redIsAI){
            makeMove(redAI.aiMove());
        } else if (currentPlayer == BLACK && blackIsAI){
            makeMove(blackAI.aiMove());
        }

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

    private void checkValidMoves(ArrayList<Move> moveList, Board boardIn, int player) {
        killAvailable = false;
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

    private void makeMove(Move move) {
        System.out.println("Player " + playerNames[currentPlayer] + " picked the following " + move);
        gameBoard.pieceMover(move, false);
        blackScore = gameBoard.getBlackPieceLocations().size();
        redScore = gameBoard.getRedPieceLocations().size();
        System.out.println("Score: Black - " + blackScore + ", Red - " + redScore);
        if (redScore * blackScore == 0) {
            System.out.println("Game Over! Player " + (redScore == 0 ? "Black" : "Red") + " won!");
            JOptionPane.showMessageDialog(null, "Game Over! Player " + (redScore == 0 ? "Black" : "Red") + " won!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
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
        if (validMoves.size() == 0){
            System.out.println("Game Over");
            JOptionPane.showMessageDialog(null, "Game Over", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }
        printValidMoves();
        if (currentPlayer == BLACK && blackIsAI){
            makeMove(blackAI.aiMove());
        }
        if (currentPlayer == RED && redIsAI) {
            makeMove(redAI.aiMove());
        }
        System.out.println("current player: " + currentPlayer);
    }

    private void printMove(Move move) {
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
            System.out.println(move.toString());
        }
        if (killAvailable){
            System.out.println("Jump available...");
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
                    }
                    g.fillRect(loc.yLocation * 720 / 8, loc.xLocation * 720 / 8, 720 / 8, 720 / 8);
                }
            }
            for (CheckerLocation loc : killDestinations){
                if (loc.equals(selectedBlock)){
                    g.setColor(new Color(160, 32, 240));
                } else {
                    g.setColor(Color.red);
                }
                g.fillRect(loc.yLocation * 720 / 8, loc.xLocation * 720 / 8, 720 / 8, 720 / 8);
            }
        }
        for (CheckerLocation loc : gameBoard.getBlackPieceLocations()) {
            if (gameBoard.getBoardPieces()[loc.xLocation][loc.yLocation].isKing()) {
                drawChecker(loc.yLocation, loc.xLocation, g, Color.lightGray);
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
            if (selectedMoves.size() != 0){
                if (checkSameInitEnd(clickedLocation)){
                    ArrayList<Move> selectionList = new ArrayList<>();
                    for (Move move : selectedMoves){
                        if (move.jumps.get(move.jumps.size() - 1).equals(clickedLocation)){
                            selectionList.add(move);
                        }
                    }
                    System.out.println("Multiple moves...");
                    String[] moveOptions = new String[selectionList.size()];
                    for (int i = 0; i < selectionList.size(); i++) {
                        moveOptions[i] = selectionList.get(i).toString();
                    }
                    String selectedMoveString = moveSelection(moveOptions);
                    for (int i = 0; i < selectionList.size(); i++){
                        if (moveOptions[i].equals(selectedMoveString)){
                            makeMove(selectionList.get(i));
                            updateBoard();
                            return;
                        }
                    }
                } else {
                    for (Move move : selectedMoves) {
                        CheckerLocation dest = move.jumps.get(move.jumps.size() - 1);
                        if (dest.equals(clickedLocation)){
                            makeMove(move);
                            updateBoard();
                            return;
                        }
                    }
                }
            }
            if (gameBoard.getBoardPieces()[clickedLocation.xLocation][clickedLocation.yLocation].getPieceColor() == currentPlayer) {
                selectedBlock = new CheckerLocation(clickedLocation);
                selectedMoves = new ArrayList<>();
                killDestinations = new ArrayList<>();
                for (Move move: validMoves){
                    if (move.xSource == selectedBlock.xLocation && move.ySource == selectedBlock.yLocation){
                        selectedMoves.add(move);
                        if (move.moveType == MOVE_KILL){
                            killDestinations.add(new CheckerLocation(move.jumps.get(move.jumps.size() - 1)));
                        }
                    }
                }
            }
            repaint();
        }
    }

    private boolean checkSameInitEnd(CheckerLocation dest){
        int numMoves = 0;
        for (Move move : selectedMoves){
            if (move.jumps.get(move.jumps.size() - 1).equals(dest)){
                numMoves++;
            }
        }
        if (numMoves > 1){
            return true;
        } else {
            return false;
        }
    }

    private String moveSelection (String[] movesIn) {
        return (String)JOptionPane.showInputDialog(null, "Multiple moves for the selected piece terminate here. Please select one.", "Select a move", JOptionPane.INFORMATION_MESSAGE, null, movesIn, movesIn[0]);
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
