package backend;

public class Game {
    private String[] playerNames;
    private int[] playerStars;//Aantal sterren zal met een formule worden omgezet naar rank en kleur. Zie joris.basdon.net/avatarcarto

    private final String[] STANDARDELEMENTS;
    private final char[] CHARELEMENTS;
    private int[][] moves;
    private int currentPlayer;
    private int currentMove;
    private int[][] elementsLeft;
    private int[] score;
    private int firstplayerElement;
    private int secondplayerElement;

    public Game(String[] playerNames, int[] playerStars) {
        STANDARDELEMENTS = new String[]{"Air", "Earth", "Fire", "Water", "Defense"};
        CHARELEMENTS = new char[] {'A', 'E', 'F', 'W', 'D'};
        this.playerNames = new String[playerNames.length];
        this.playerStars = new int[playerStars.length];
        for(int i = 0; i < playerNames.length; i++) {
            this.playerNames[i] = playerNames[i];
            this.playerStars[i] = playerStars[i];
        }
        startNewGame();
    }

    private void startNewGame() {
        moves = new int[2][9];
        currentPlayer = 0;
        currentMove = 0;
        elementsLeft = new int[][]{
            {2, 2, 2, 2, 1},
            {2, 2, 2, 2, 1}
        };
        score = new int[]{0, 0};
    }

    private void doMove(int imove) {
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

    private String moveToString(int move) {
        return STANDARDELEMENTS[move];
    }

    private char moveToChar(int move) {
        return CHARELEMENTS[move];
    }
}
