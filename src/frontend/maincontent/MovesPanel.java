package frontend.maincontent;

import frontend.FrontendController;
import frontend.GameChangeListener;
import frontend.Main;
import frontend.components.DefaultTitledBorder;
import frontend.components.TripleBorder;

import javax.swing.*;
import javax.swing.border.*;

import backend.Game;
import backend.Game.Data;

import java.awt.*;

import static common.Constants.CHARELEMENTS;

class MovesPanel extends JPanel implements GameChangeListener
{
	private final JLabel[][] playerLabels;
	private static final Color[] RESULTCOLORS = { new Color(0xFF0000), new Color(0x0), new Color(0x008000) };

	MovesPanel(FrontendController controller)
	{
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

		controller.addGameChangeListener(this);
	}

	@Override
	public void onGameStart(Data data)
	{
		for (int i = 0; i < 9; i++) {
			for (int p = 0; p < 2; p++) {
				final JLabel l = this.playerLabels[p][i];
				l.setText("?");
				l.setForeground(RESULTCOLORS[1]);
			}
		}
	}

	@Override
	public void onGameChange(Game.Data data)
	{
		for (int i = 0; i < 9; i++) {
			final int moveResult = data.getMoveScore(i);
			for (int p = 0; p < 2; p++) {
				final JLabel l = this.playerLabels[p][i];
				if (data.getCurrentMove() <= i) {
					l.setText("?");
					l.setForeground(RESULTCOLORS[1]);
					continue;
				}

				l.setText(String.valueOf(CHARELEMENTS[data.getMove(p, i)]));
				l.setForeground(RESULTCOLORS[moveResult * ((p * -1) | 1) + 1]);
			}
		}
	}

	@Override
	public void onGameEnd(Data data)
	{
	}

}
