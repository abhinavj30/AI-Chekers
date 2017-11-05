package client;

import client.CheckersGame;

/**
 * Created by abhinav on 10/19/2017.
 */
public class RunGame {

    public static CheckersGame newGame;

    public static void main(String[] args) {
        System.out.print("Starting game...");
        newGame = new CheckersGame();
    }
}
