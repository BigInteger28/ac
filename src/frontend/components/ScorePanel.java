package frontend.components;

import frontend.Main;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.*;

public class ScorePanel extends JPanel {

    private final JLabel label1;
    private final JLabel label2;

    public ScorePanel() {
        this.setLayout(new GridLayout(2, 0, 0, 5));
        final Border titleBorder = new DefaultTitledBorder("Score");
        final Border outerBorder = new EmptyBorder(5, 5, 5, 0);
        final Border innerBorder = new EmptyBorder(2, 9, 4, 9);
        this.setBorder(TripleBorder.create(outerBorder, titleBorder, innerBorder));
        this.label1 = new JLabel();
        this.label2 = new JLabel();
        label1.setFont(Main.monospaceFont);
        label2.setFont(Main.monospaceFont);
        this.add(label1);
        this.add(label2);
        this.setScore(0, 0);
    }

    public void setScore(int p1, int p2) {
        this.label1.setText("Player 1:   " + p1);
        this.label2.setText("Player 2:   " + p2);
    }

}
