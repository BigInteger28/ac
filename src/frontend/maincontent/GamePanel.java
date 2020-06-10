package frontend.maincontent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import frontend.FrontendController;

import java.awt.*;

class GamePanel extends JPanel
{
	private final MovesPanel movesPanel;
	private final ScorePanel scorePanel;
	private final PlayerControl[] playerControls;

	GamePanel(FrontendController controller)
	{
		this.setLayout(new BorderLayout());

		this.movesPanel = new MovesPanel(controller);
		this.scorePanel = new ScorePanel(controller);
		this.playerControls = new PlayerControl[] { new PlayerControl("Player 1", 0, controller),
			new PlayerControl("Player 2", 1, controller), };

		this.setBorder(new EmptyBorder(10, 10, 10, 10));

		final JPanel innerPanel = new JPanel(new BorderLayout());
		innerPanel.add(this.movesPanel, BorderLayout.CENTER);
		innerPanel.add(this.scorePanel, BorderLayout.EAST);
		this.add(innerPanel, BorderLayout.CENTER);
		this.add(this.playerControls[0], BorderLayout.NORTH);
		this.add(this.playerControls[1], BorderLayout.SOUTH);
	}

}
