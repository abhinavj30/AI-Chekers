package client;

import javax.swing.*;

/**
 * Created by abhinav on 10/19/2017.
 */
public class RunGame extends JFrame {

    public static CheckersGame newGame;

    public static void main(String[] args) {
        System.out.println("Starting game...");
        String[] options = {"AI, AI", "AI, Human", "Human, AI", "Human, Human"};
        Integer[] timeOptions = new Integer[57];
        for (int i = 0; i < 57; i++){
            timeOptions[i] = i + 3;
        };
        int aiTime = 3;
        int playerConfig = JOptionPane.showOptionDialog(null, "Pick player configuration", "Options", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (playerConfig != 3){
            aiTime = (int) JOptionPane.showInputDialog(null, "Select AI time limit (yes, I'm that lazy)", "AI Time", JOptionPane.INFORMATION_MESSAGE, null, timeOptions, timeOptions[0]);
        }

        switch (playerConfig) {
            case 0:
                newGame = new CheckersGame(true, true, aiTime);
                break;
            case 1:
                newGame = new CheckersGame(true, false, aiTime);
                break;
            case 2:
                newGame = new CheckersGame(false, true, aiTime);
                break;
            case 3:
                newGame = new CheckersGame(false, false, aiTime);
                break;
            default:
                break;
        }
    }
}
