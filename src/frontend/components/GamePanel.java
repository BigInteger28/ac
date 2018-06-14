package frontend.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.BiConsumer;

public class GamePanel extends JPanel {

    private final MovesPanel movesPanel;
    private final ScorePanel scorePanel;
    private final PlayerControl[] playerControls;

    public GamePanel(BiConsumer<Integer, Integer> elementChooseListener) {
        this.setLayout(new BorderLayout());

        this.movesPanel = new MovesPanel();
        this.scorePanel = new ScorePanel();
        this.playerControls = new PlayerControl[] {
            new PlayerControl("Player 1", 0, elementChooseListener),
            new PlayerControl("Player 2", 1, elementChooseListener),
        };

        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        final JPanel innerPanel = new JPanel(new BorderLayout());
        innerPanel.add(this.movesPanel, BorderLayout.CENTER);
        innerPanel.add(this.scorePanel, BorderLayout.EAST);
        this.add(innerPanel, BorderLayout.CENTER);
        this.add(this.playerControls[0], BorderLayout.NORTH);
        this.add(this.playerControls[1], BorderLayout.SOUTH);
    }

    public MovesPanel getMovesPanel() {
        return this.movesPanel;
    }

    public ScorePanel getScorePanel() {
        return this.scorePanel;
    }
    
    public void setElementsLeft(int[][] elementsLeft) {
        for (int i = 0; i < 2; i++) {
            this.playerControls[i].setElementsLeft(elementsLeft[i]);
        }
    }

}
