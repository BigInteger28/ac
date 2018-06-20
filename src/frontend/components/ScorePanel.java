package frontend.components;

import frontend.FrontendController;
import frontend.GameChangeListener;
import frontend.GameState;
import frontend.Main;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.*;

class ScorePanel extends JPanel implements GameChangeListener
{
    private final JLabel label1;
    private final JLabel label2;

    ScorePanel(FrontendController controller)
    {
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
        
        controller.addGameChangeListener(this);
    }

    @Override
    public void onGameChanged(GameState newState)
    {
        this.label1.setText("Player 1:   " + newState.score[0]);
        this.label2.setText("Player 2:   " + newState.score[1]);
    }

}
