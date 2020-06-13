package frontend.dialogs;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Window;
import java.util.ArrayList;

import javax.swing.*;

import backend.Database;
import backend.Game;
import backend.Player;
import common.Constants;
import frontend.components.PlayerSelector;
import frontend.util.SwingUtil;

public class SecondOpinionDialog extends JDialog implements PlayerSelector.Listener, Runnable
{
	private static Player lastChosenPlayer;
	private static Database lastChosenDatabase;

	private PlayerSelector selector;
	private JLabel lblJudgement;

	private Game game;
	private boolean updateQueued;

	public SecondOpinionDialog(Window parentWindow,
		ArrayList<Player> players, ArrayList<Database> databases,
		Game game)
	{
		super(parentWindow);
		this.game = game;

		this.selector = new PlayerSelector(players, databases, lastChosenPlayer, lastChosenDatabase);
		this.selector.listener = this;

		this.lblJudgement = new JLabel("Choose a player", SwingConstants.CENTER);

		this.getContentPane().setLayout(new BorderLayout());
		this.getContentPane().add(this.selector, BorderLayout.CENTER);
		this.getContentPane().add(this.lblJudgement, BorderLayout.SOUTH);

		this.setTitle("Second opinion");
		this.pack();
		this.setModal(true);
		this.setMinimumSize(this.getSize());
		this.setLocationRelativeTo(parentWindow);

		this.selector.requestFocusInWindow();
		this.queueUpdate();

		this.setVisible(true);
		this.dispose();
	}

	@Override
	public void requestCancel(PlayerSelector source)
	{
		SwingUtil.close(this);
	}

	@Override
	public void acceptCurrentChoices(PlayerSelector source)
	{
	}

	@Override
	public void run()
	{
		int p1chosenElement, p2chosenElement;
		Player p;
		Database db;

		this.updateQueued = false;

		p = this.selector.getSelectedPlayer();
		db = this.selector.getSelectedDatabase();

		if (p == null) {
			return;
		}
		if (!p.canUseDatabase()) {
			db = null;
		}

		try {
			p.load();
		} catch (Exception e) {
			e.printStackTrace();
			this.lblJudgement.setForeground(Color.RED);
			this.lblJudgement.setText("Failed to load player: " + e.toString());
			return;
		}

		if (db != null) {
			try {
				db.load();
			} catch (Exception e) {
				e.printStackTrace();
				this.lblJudgement.setForeground(Color.RED);
				this.lblJudgement.setText("Failed to load database: " + e.toString());
				return;
			}
		}

		this.game.p1 = p;
		this.game.db1 = db;
		lastChosenPlayer = p;
		lastChosenDatabase = db;
		p1chosenElement = p.doMove(0, db, this.game.data);
		p2chosenElement = p.doMove(1, db, this.game.data);
		this.lblJudgement.setForeground(Color.black);
		this.lblJudgement.setText(String.format(
			"%s would play:\n for player 1: %c\n for player 2: %c",
			this.game.getPlayer1WithDatabaseName(),
			Constants.CHARELEMENTS[p1chosenElement],
			Constants.CHARELEMENTS[p2chosenElement]
		));
	}

	private void queueUpdate()
	{
		if (!this.updateQueued) {
			this.updateQueued = true;
			SwingUtilities.invokeLater(this);
		}
	}

	@Override
	public void selectedPlayerChanged(PlayerSelector source)
	{
		this.queueUpdate();
	}

	@Override
	public void selectedDatabaseChanged(PlayerSelector source)
	{
		this.queueUpdate();
	}
}
