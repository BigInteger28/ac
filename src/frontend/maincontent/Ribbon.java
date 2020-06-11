package frontend.maincontent;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import frontend.FrontendController;
import frontend.dialogs.LocationDialog;
import frontend.util.SwingMsg;
import resources.Resources;

public class Ribbon extends JTabbedPane
{
	private static final String N = SpringLayout.NORTH;
	private static final String E = SpringLayout.EAST;
	private static final String S = SpringLayout.SOUTH;
	private static final String W = SpringLayout.WEST;
	private static final int PADDING = 5;

	private final FrontendController controller;

	public Ribbon(FrontendController controller)
	{
		this.controller = controller;
		this.addTab("Game", this.createGameMenu());
		this.addTab("Settings", this.createSettingsMenu());
	}

	private JPanel createGameMenu()
	{
		final SpringLayout layout = new SpringLayout();
		final JPanel pnl = new JPanel(layout);

		final JButton btnNewGame = new JButton("New game");
		btnNewGame.addActionListener(e -> this.controller.startNewGame());
		pnl.add(btnNewGame);

		final JButton btnAdvGame = new JButton("adv");
		btnAdvGame.addActionListener(e -> this.controller.startNewGameAdv());
		pnl.add(btnAdvGame);

		layout.putConstraint(W, btnNewGame, PADDING, W, pnl);
		layout.putConstraint(N, btnNewGame, PADDING, N, pnl);
		layout.putConstraint(S, pnl, PADDING, S, btnNewGame);

		layout.putConstraint(N, btnAdvGame, 0, N, btnNewGame);
		layout.putConstraint(W, btnAdvGame, 0, E, btnNewGame);
		layout.putConstraint(E, pnl, -PADDING, E, btnAdvGame);

		return pnl;
	}

	private JPanel createSettingsMenu()
	{
		final JPanel pnl = new JPanel(new FlowLayout());

		final JButton btnLocations = new JButton("Engine locations");
		btnLocations.addActionListener(e -> LocationDialog.show(this.controller));
		pnl.add(btnLocations);

		JButton btnWorkingdir = new JButton("Workingdir");
		btnWorkingdir.addActionListener(e -> SwingMsg.err_ok(btnWorkingdir, "a", Resources.workingdir.getAbsolutePath()));
		pnl.add(btnWorkingdir);

		return pnl;
	}

}
