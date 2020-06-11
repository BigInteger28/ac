package frontend.maincontent;

import javax.swing.*;

import java.awt.*;

public class ScorePanel extends JPanel
{
	private final JLabel label1;
	private final JLabel label2;

	public ScorePanel()
	{
		this.setLayout(new GridLayout(2, 0, 0, 5));
		this.label1 = new JLabel();
		this.label2 = new JLabel();
		this.updateLabels(0, 0);
		this.add(label1);
		this.add(label2);
	}

	public void updateLabels(int score1, int score2)
	{
		this.label1.setText("Player 1:   " + score1);
		this.label2.setText("Player 2:   " + score2);
	}
}
