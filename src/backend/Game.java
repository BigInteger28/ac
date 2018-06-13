package backend;

import static common.Constants.*;

public class Game {
    private String[] playerNames;
    private int[] playerStars;//Aantal sterren zal met een formule worden omgezet naar rank en kleur. Zie joris.basdon.net/avatarcarto

    private int[][] moves;
    private int currentPlayer;
    private int currentMove;
    private int[][] elementsLeft;
    private int[] score;
    private int firstplayerElement;
    private int secondplayerElement;

    public Game(String[] playerNames, int[] playerStars) {
        this.playerNames = new String[playerNames.length];
        this.playerStars = new int[playerStars.length];
        for(int i = 0; i < playerNames.length; i++) {
            this.playerNames[i] = playerNames[i];
            this.playerStars[i] = playerStars[i];
        }
        startNewGame();
    }

    public void startNewGame() {
        moves = new int[2][9];
        currentPlayer = 0;
        currentMove = 0;
        elementsLeft = new int[][]{
            {2, 2, 2, 2, 1},
            {2, 2, 2, 2, 1}
        };
        score = new int[]{0, 0};
    }

    public void doMove(int imove) {
        int move = imove;
        while (elementsLeft[currentPlayer][move] < 1) {
            move = (move + 1) % 4;
        }

        elementsLeft[currentPlayer][move]--;
        moves[currentPlayer][currentMove] = move;

        if (currentPlayer == 0) {
            firstplayerElement = move;
        } else {
            secondplayerElement = move;
            doScore();
            currentMove++;
        }
        currentPlayer = (currentPlayer + 1) % 2;
    }

    private void doScore() {
        if (result() == 1) score[0]++;
        if (result() == -1) score[1]++;
    }

    private int result() {
        int[][] result = {
            {0, -1, 0, 1, 0},//Air
            {1, 0, -1, 0, 0},//Earth
            {0, 1, 0, -1, 0},//Fire
            {-1, 0, 1, 0, 0},//Water
            {0, 0, 0, 0, 0},//Defense
        };
        return result[firstplayerElement][secondplayerElement];
    }

    private void prevMove() {
        elementsLeft[0][moves[0][currentMove]]++;
        elementsLeft[1][moves[1][currentMove]]++;
        moves[0][currentMove] = 0;
        moves[1][currentMove] = 0;
        currentPlayer = 0;
        currentMove -= 1; //controle gebeurt in front-end
    }

    private char moveToChar(int move) {
        return CHARELEMENTS[move];
    }

    public int getMove(int player, int move) {
        return moves[player][move];
    }

    public int getScore(int player) {
        return score[player];
    }

    public int getPreviousPlayer() {
        //return currentPlayer ^ 1;
        return (currentPlayer + 1) % 2; //Soms is het gevaarlijk om negatieve modulos te nemen. Bad practice
        /*
        0 ^ 1 = 1
        1 ^ 1 = 0
         */
    }

    public int getCurrentMove() {
        return currentMove;
    }

    public int[][] getElementsLeft() {
        return elementsLeft;
    }

}
