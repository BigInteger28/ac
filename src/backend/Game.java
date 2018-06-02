package backend;

public class Game {
    private String firstplayerName;
    private int firstplayerStars;//Aantal sterren wordt omgezet naar rank en kleur
    private String secondplayerName;
    private int secondplayerStars;

    private final String[] STANDARDELEMENTS;
    private int[][] moves;
    private int currentPlayer;
    private int currentMove;
    private int[][] elementsLeft;
    private int[] score;

    public Game() {
        STANDARDELEMENTS = new String[]{"Defense", "Air", "Earth", "Fire", "Water"};
        startNewGame();
    }

    private void startNewGame() {
        moves = new int[2][9];
        currentPlayer = 0;
        currentMove = 0;
        elementsLeft = new int[][]{
            {1, 2, 2, 2, 2},
            {1, 2, 2, 2, 2}
        };
        score = new int[]{0, 0};
    }

    private void doMove(int move) {
        //controle op legaliteit zal in de frontend gebeuren. Maar je moet ergens beginnen eh!
        if (elementsLeft[currentPlayer][move] > 0) {
            elementsLeft[currentPlayer][move]--;
            moves[currentPlayer][currentMove] = move;
        }
        if (currentPlayer == 1) currentMove++;
        currentPlayer = (currentPlayer + 1) % 2;
    }
    
    private int result(int firstplayerElement, int secondplayerElement) {
        int[][] result = {
            {0, 0, 0, 0, 0},//Defense
            {0, 0, -1, 0, 1},//Air
            {0, 1, 0, -1, 0},//Earth
            {0, 0, 1, 0, -1},//Fire
            {0, -1, 0, 1, 0}//Water
        };
        return result[firstplayerElement][secondplayerElement];
    }
}
