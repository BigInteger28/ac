package frontend;

import common.Constants;
import frontend.components.GamePanel;

import javax.swing.*;
import java.awt.*;

import static common.Constants.*;

public class Main {

    public static Font monospaceFont;

    private final GamePanel gamePanel;

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
    }

    private void onElementChosen(int playerNumber, int element) {
        System.out.printf("player %d chose element %d (%s)%n", playerNumber, element, Constants.STANDARDELEMENTS[element]);
        this.gamePanel.getMovesPanel().setMove(1, 3, WATER);
        this.gamePanel.getScorePanel().setScore(5, 7);
    }

}
