package frontend.maincontent;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import frontend.FrontendController;
import frontend.dialogs.LocationDialog;
import frontend.util.SwingMsg;
import resources.Resources;

public class Ribbon extends JTabbedPane
{
	private final FrontendController controller;

	public Ribbon(FrontendController controller)
	{
		this.controller = controller;
		this.addTab("Game", this.createGameMenu());
		this.addTab("Settings", this.createSettingsMenu());
	}

	private JPanel createGameMenu()
	{
		final JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));

		final JButton btnNewGame = new JButton("New game");
		btnNewGame.addActionListener(e -> this.controller.startNewGame());
		pnl.add(btnNewGame);

		final JButton btnAdvGame = new JButton("adv");
		btnAdvGame.addActionListener(e -> this.controller.startNewGameAdv());
		pnl.add(btnAdvGame);

		final JButton btnUndo = new JButton("Undo move");
		btnUndo.addActionListener(e -> this.controller.undoMove());
		pnl.add(btnUndo);

		return pnl;
	}

	private JPanel createSettingsMenu()
	{
		final JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));

		final JButton btnLocations = new JButton("Engine locations");
		btnLocations.addActionListener(e -> LocationDialog.show(this.controller));
		pnl.add(btnLocations);

		JButton btnWorkingdir = new JButton("Workingdir");
		btnWorkingdir.addActionListener(e -> SwingMsg.err_ok(btnWorkingdir, "a", Resources.workingdir.getAbsolutePath()));
		pnl.add(btnWorkingdir);

		return pnl;
	}

}
