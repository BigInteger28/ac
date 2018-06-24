package frontend.maincontent;

import frontend.FrontendController;
import frontend.GameChangeListener;
import frontend.Main;
import frontend.components.DefaultTitledBorder;
import frontend.components.TripleBorder;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import backend.Game;
import backend.Game.Data;

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
        this.updateLabels(0, 0);
        this.add(label1);
        this.add(label2);
        
        controller.addGameChangeListener(this);
    }
    
    private void updateLabels(int score1, int score2)
    {
        this.label1.setText("Player 1:   " + score1);
        this.label2.setText("Player 2:   " + score2);
    }

    @Override
    public void onGameStart(Data data)
    {
        this.updateLabels(0, 0);
    }

    @Override
    public void onGameChange(Game.Data data)
    {
        this.updateLabels(data.getScore(0), data.getScore(1));
    }

    @Override
    public void onGameEnd(Data data)
    {
    }

}
