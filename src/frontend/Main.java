package frontend;

import backend.Engine;
import backend.Game;
import common.Constants;
import frontend.components.GamePanel;
import frontend.components.MovesPanel;

import javax.swing.*;
import java.awt.*;

public class Main {

    public static Font monospaceFont;

    private final GamePanel gamePanel;
    private final Game game;
    private final Engine engine;
    public static void main(String[] args) {
        //AFBLIJVEN, ROBIN WAAKT HIER!!!

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        monospaceFont = new Font("Courier New", Font.PLAIN, 12);
        SwingUtilities.invokeLater(Main::new);
    }

    public Main() {
        this.gamePanel = new GamePanel(this::onElementChosen);
        final JFrame frame = new JFrame("Avatar Carto Java Edition");
        frame.setContentPane(this.gamePanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(frame.getSize());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        //Joris tryout
        String[] players = new String[] {"Joris", "Stockfish"};
        int[] stars = new int[] {42, 112};
        game = new Game(players, stars);
        int[] stockfishSoftware = new int[] {4, 17, 36, 12, 94, 28, 147, 12, 7};
        engine = new Engine(stockfishSoftware);

        //Speler drukt op NEW GAME (tegen engine stockfish in ons geval)
        this.startNewGame();
    }
    
    private void startNewGame() {
        this.game.startNewGame();
        this.gamePanel.getMovesPanel().resetMoves();
    }

    private void onElementChosen(int playerNumber, int element) {
        System.out.printf("player %d chose element %d (%s)%n", playerNumber, element, Constants.STANDARDELEMENTS[element]);
        //this.gamePanel.getMovesPanel().setMove(1, 3, WATER);
        //this.gamePanel.getScorePanel().setScore(5, 7);

        if (playerNumber != 0) {
            return;
        }

        //Joris tryout
        this.game.doMove(element);
        int move = game.getCurrentMove();
        int playerPreviousElement = 0;
        if (move > 0) {
            playerPreviousElement = game.getMove(0, move - 1);
        }
        this.game.doMove(this.engine.getElement(move, playerPreviousElement));
        
        this.updateMovesDisplay();
        this.gamePanel.setElementsLeft(this.game.getElementsLeft());
    }

    private void updateMovesDisplay() {
        final int lastMove = this.game.getCurrentMove() - 1;
        if (lastMove < 0) {
            return;
        }
        final MovesPanel movesDisplay = this.gamePanel.getMovesPanel();
        for (int player = 0; player < 2; player++) {
            movesDisplay.setMove(player, lastMove, this.game.getMove(player, lastMove));
        }
        movesDisplay.setMoveScore(lastMove, this.game.getMoveResult(lastMove));
        final int p1score = this.game.getScore(0);
        final int p2score = this.game.getScore(1);
        this.gamePanel.getScorePanel().setScore(p1score, p2score);
    }

}
