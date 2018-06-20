package frontend;

import backend.Game;

import java.util.Arrays;;

public class GameState
{
    public final int currentMove;
    public final int score[];
    public final int[][] elementsLeft;
    public final int[][] moves;
    public final int[] moveScores;
    
    public GameState()
    {
        this.currentMove = 0;
        this.score = new int[] { 0, 0 };
        this.elementsLeft = new int[][] { new int[5], new int[5] };
        this.moves = new int[][] { new int[9], new int[9] };
        this.moveScores = new int[9];
        
        Arrays.fill(elementsLeft[0], 0);
        Arrays.fill(elementsLeft[1], 0);
        Arrays.fill(this.moveScores, 0);
    }

    public GameState(Game game)
    {
        this.currentMove = game.getCurrentMove();
        this.score = new int[] { game.getScore(0), game.getScore(1) };
        this.elementsLeft = new int[][] { new int[5], new int[5] };
        final int[][] gameElementsLeft = game.getElementsLeft();
        System.arraycopy(gameElementsLeft[0], 0, this.elementsLeft[0], 0, 5);
        System.arraycopy(gameElementsLeft[1], 0, this.elementsLeft[1], 0, 5);
        this.moveScores = new int[9];
        this.moves = new int[][] { new int[9], new int[9] };
        for (int move = 0; move < 9; move++) {
            this.moveScores[move] = game.getMoveResult(move);
            for (int player = 0; player < 2; player++) {
                this.moves[player][move] = game.getMove(player, move);
            }
        }
    }

}
