package frontend.components;

import common.Constants;
import frontend.Main;

import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;

public class MovesPanel extends JPanel {

    private final JLabel[][] playerLabels;

    public MovesPanel() {
        this.setLayout(new BorderLayout());
        final Border titleBorder = new DefaultTitledBorder("Game");
        final Border outerBorder = new EmptyBorder(5, 0, 5, 5);
        final Border innerBorder = new EmptyBorder(2, 9, 4, 9);
        this.setBorder(TripleBorder.create(outerBorder, titleBorder, innerBorder));

        this.playerLabels = new JLabel[2][];
        this.playerLabels[0] = new JLabel[9];
        this.playerLabels[1] = new JLabel[9];

        final JLabel p1 = new JLabel("Player 1:");
        final JLabel p2 = new JLabel("Player 2:");
        p1.setFont(Main.monospaceFont);
        p2.setFont(Main.monospaceFont);

        final JPanel playerPanel = new JPanel();
        playerPanel.setLayout(new GridLayout(2, 0, 0, 5));
        playerPanel.add(p1);
        playerPanel.add(p2);
        playerPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
        this.add(playerPanel, BorderLayout.WEST);

        final JPanel mp = new JPanel(new GridLayout(2, 9, 5, 5));
        for (int a = 0; a < 2; a++) {
            for (int i = 0; i < 9; i++) {
                this.playerLabels[a][i] = new JLabel("?");
                this.playerLabels[a][i].setFont(Main.monospaceFont);
                mp.add(this.playerLabels[a][i]);
            }
        }

        this.add(mp, BorderLayout.CENTER);
    }

    public void setMove(int playerNumber, int zet, int element) {
        final String elementstr = String.valueOf(Constants.CHARELEMENTS[element]);
        this.playerLabels[playerNumber][zet].setText(elementstr);
    }
    
    public void resetMoves() {
        for (int a = 0; a < 2; a++) {
            for (int b = 0; b < 9; b++) {
                this.playerLabels[a][b].setText("?");
            }
        }
    }

}
