package client;

import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by abhinav on 10/19/2017.
 */
public class RunGame extends JFrame {

    public static CheckersGame newGame;

    public static void main(String[] args) {
        System.out.println("Starting game...");
        String[] options = {"AI, AI", "AI, Human", "Human, AI", "Human, Human"};
        Integer[] timeOptions = new Integer[57];
        for (int i = 0; i < 57; i++) {
            timeOptions[i] = i + 3;
        }
        ;
        int aiTime = 3;
        System.out.println("Please select player configuration");
        int playerConfig = JOptionPane.showOptionDialog(null, "Pick player configuration", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        System.out.println("Player Black is " + ((playerConfig == 0 || playerConfig == 1) ? "AI" : "human") + "; Player Red is " + ((playerConfig == 0 || playerConfig == 2) ? "AI" : "human"));
        if (playerConfig != 3) {
            try {
                System.out.println("Please select AI time limit");
                aiTime = (int) JOptionPane.showInputDialog(null, "Select AI time limit (yes, I'm that lazy)", "AI Time", JOptionPane.INFORMATION_MESSAGE, null, timeOptions, timeOptions[0]);
            } catch (NullPointerException e) {
                aiTime = 3;
            }
            System.out.println("AI time limit has been set to " + aiTime + " seconds");
        }


        int currentPlayer = 1;
        Board boardLoaded = new Board();

        int loadFile = JOptionPane.showConfirmDialog(null, "Load game from a file?", "Load from file?", JOptionPane.INFORMATION_MESSAGE, JOptionPane.YES_NO_OPTION);
        if (loadFile == 0) {

            boardLoaded = new Board();
            String fileLocation = JOptionPane.showInputDialog(null, "Please enter file address", "Enter File Address", JOptionPane.DEFAULT_OPTION);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(fileLocation));
                String readLine;
                int iLoc = 0;
                while ((readLine = reader.readLine()) != null) {
                    if (iLoc < 8) {
                        String[] linePieces = readLine.trim().split("\\s+");
                        for (int jRead = 0; jRead < 4; jRead++) {
                            int piece = Integer.parseInt(linePieces[jRead]);
                            int jLoc = (2 * jRead) + ((iLoc + 1) % 2);

                            if (piece > 4 || piece < 0) {
                                throw new Exception("Error: file format not correct.");
                            } else {
                                switch (piece) {
                                    case 1:
                                        boardLoaded.addPiece(1, iLoc, jLoc);
                                        break;
                                    case 2:
                                        boardLoaded.addPiece(2, iLoc, jLoc);
                                        break;
                                    case 3:
                                        boardLoaded.addPiece(1, iLoc, jLoc, true);
                                        break;
                                    case 4:
                                        boardLoaded.addPiece(2, iLoc, jLoc, true);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    } else if (iLoc == 8){
                        String[] currentPlayerString = readLine.trim().split("\\s+");
                        currentPlayer = Integer.parseInt(currentPlayerString[0]);
                    } else if (iLoc == 9){
                        String[] aiTimeString = readLine.trim().split("\\s+");
                        aiTime = Integer.parseInt(aiTimeString[0]);
                    }
                    iLoc++;
                }
                System.out.println("Loaded file.");
                if (playerConfig != 3) {
                    System.out.println("New AI time: " + aiTime);
                }
                System.out.println("Current player loaded: " + (currentPlayer == 1 ? "Black" : "Red"));

            } catch (IOException e) {
                System.out.println("Could not open file \"" + fileLocation + "\". Loading default board layout...");
                boardLoaded.initializeBoard();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("Loading default board layout...");
                boardLoaded.initializeBoard();
            }
        } else {
            boardLoaded.initializeBoard();
        }

        switch (playerConfig) {
            case 0:
                newGame = new CheckersGame(true, true, aiTime, boardLoaded, currentPlayer);
                break;
            case 1:
                newGame = new CheckersGame(true, false, aiTime, boardLoaded, currentPlayer);
                break;
            case 2:
                newGame = new CheckersGame(false, true, aiTime, boardLoaded, currentPlayer);
                break;
            case 3:
                newGame = new CheckersGame(false, false, aiTime, boardLoaded, currentPlayer);
                break;
            default:
                break;
        }
    }
}
