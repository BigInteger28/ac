package backend;

public class Game {
    private String firstplayerName;
    private int firstplayerStars;//Aantal sterren wordt omgezet naar rank en kleur
    private String secondplayerName;
    private int secondplayerStars;

    private String[] standardElements;
    private int[][] moves;
    private int currentMove;

    public Game() {
        standardElements = new String[]{"Defense", "Air", "Earth", "Fire", "Water"};
        startNewGame();
    }

    private void startNewGame() {
        moves = new int[2][9];
        currentMove = 0;
    }

    private boolean isMoveLegal(int player, int move) {
        boolean isLegal = true;
        int[] elementsLeft = new int[]{1, 2, 2, 2, 2};

        for (int i = 0; i < currentMove; i++) {
            elementsLeft[moves[player][i]]--;
            if(elementsLeft[moves[player][i]] < 1) break;
        }
        return isLegal;
    }

    private void doMove(int player, int move) {
        if(isMoveLegal(player, move)) moves[player][currentMove] = move;
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
