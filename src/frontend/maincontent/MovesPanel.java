package frontend.maincontent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import backend.Game;

import java.awt.*;

import static common.Constants.CHARELEMENTS;

public class MovesPanel extends JPanel
{
	private final JLabel[][] playerLabels;
	private static final Color[] RESULTCOLORS = { new Color(0xFF0000), new Color(0x0), new Color(0x008000) };

	public MovesPanel()
	{
		this.setLayout(new BorderLayout());

		this.playerLabels = new JLabel[2][];
		this.playerLabels[0] = new JLabel[9];
		this.playerLabels[1] = new JLabel[9];

		final JLabel p1 = new JLabel("Player 1:");
		final JLabel p2 = new JLabel("Player 2:");

		final JPanel playerPanel = new JPanel();
		playerPanel.setLayout(new GridLayout(2, 0, 0, 5));
		playerPanel.add(p1);
		playerPanel.add(p2);
		playerPanel.setBorder(new EmptyBorder(0, 0, 0, 10));
		this.add(playerPanel, BorderLayout.WEST);

		final JPanel mp = new JPanel(new GridLayout(2, 9, 5, 5));
		for (int a = 0; a < 2; a++) {
			for (int i = 0; i < 9; i++) {
				mp.add(this.playerLabels[a][i] = new JLabel("?", SwingConstants.CENTER));
			}
		}

		this.add(mp, BorderLayout.CENTER);
	}

	public void updateLabels(Game.Data gamedata)
	{
		for (int i = 0; i < 9; i++) {
			final int moveResult = gamedata.getMoveScore(i);
			for (int p = 0; p < 2; p++) {
				final JLabel l = this.playerLabels[p][i];
				if (gamedata.getCurrentMove() <= i) {
					l.setText("?");
					l.setForeground(RESULTCOLORS[1]);
					continue;
				}

				l.setText(String.valueOf(CHARELEMENTS[gamedata.getMove(p, i)]));
				l.setForeground(RESULTCOLORS[moveResult * ((p * -1) | 1) + 1]);
			}
		}
	}
}
