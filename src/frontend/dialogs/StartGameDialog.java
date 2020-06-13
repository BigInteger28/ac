package frontend.dialogs;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;

import backend.Database;
import backend.Game;
import backend.Player;
import frontend.components.PlayerSelector;
import frontend.util.SwingMsg;
import frontend.util.SwingUtil;

public class StartGameDialog extends JDialog implements
	PlayerSelector.Listener,
	ActionListener /*buttons*/
{
	private PlayerSelector left, right;
	private JButton btnOk;
	private boolean gameDataSet;
	private Game game;

	public StartGameDialog(Window parentWindow,
		ArrayList<Player> players, ArrayList<Database> databases,
		Game game)
	{
		super(parentWindow);
		this.game = game;

		JLabel lblVs;
		GridBagConstraints gbc;
		Container contentPane;

		// left
		this.left = new PlayerSelector(players, databases, game.p1, game.db1);
		this.left.listener = this;

		// vs
		lblVs = new JLabel("vs");

		// right
		this.right = new PlayerSelector(players, databases, game.p2, game.db2);
		this.right.listener = this;

		// btnOk
		this.btnOk = new JButton("Start");
		this.btnOk.addActionListener(this);

		// layout and stuff
		(contentPane = this.getContentPane()).setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets.top = 5;
		gbc.insets.left = 5;
		gbc.insets.bottom = 5;
		gbc.insets.right = 5;

		// left
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1d;
		gbc.weighty = 1d;
		contentPane.add(this.left, gbc);

		// vs
		gbc.gridx = 2;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 0d;
		gbc.weighty = 1d;
		gbc.fill = GridBagConstraints.NONE;
		contentPane.add(lblVs, gbc);
		gbc.fill = GridBagConstraints.BOTH;

		// right
		gbc.gridx = 3;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		gbc.weightx = 1d;
		gbc.weighty = 1d;
		contentPane.add(this.right, gbc);

		// buttons
		gbc.gridx = 2;
		gbc.gridy = 2;
		gbc.gridwidth = 3;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.EAST;
		contentPane.add(btnOk, gbc);

		this.setTitle("Start new game");
		this.pack();
		this.setModal(true);
		this.setMinimumSize(this.getSize());
		this.setLocationRelativeTo(parentWindow);

		this.left.requestFocusInWindow();

		this.setVisible(true);
		this.dispose();
	}

	public boolean wasGameDataSet()
	{
		return this.gameDataSet;
	}

	private void okAction()
	{
		Player p1, p2;
		Database db1, db2;
		String what;

		p1 = this.left.getSelectedPlayer();
		p2 = this.right.getSelectedPlayer();
		db1 = this.left.getSelectedDatabase();
		db2 = this.right.getSelectedDatabase();

		if (p1 == null) {
			SwingMsg.err_ok(this, this.getTitle(), "No player 1 selected");
			return;
		}
		if (p2 == null) {
			SwingMsg.err_ok(this, this.getTitle(), "No player 2 selected");
			return;
		}

		if (!p1.canUseDatabase()) {
			db1 = null;
		}
		if (!p2.canUseDatabase()) {
			db2 = null;
		}

		what = "";
		try {
			what = "player 1";
			p1.load();
			what = "player 2";
			p2.load();
			if (db1 != null) {
				what = "database 1";
				db1.load();
			}
			if (db2 != null) {
				what = "database 2";
				db2.load();
			}
		} catch (Exception e) {
			e.printStackTrace();
			SwingMsg.err_ok(this, this.getTitle(), "Error for " + what + ":\n" + e.toString());
			return;
		}

		game.p1 = p1;
		game.p2 = p2;
		game.db1 = db1;
		game.db2 = db2;

		this.gameDataSet = true;
		SwingUtil.close(this);
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		if (e.getSource() == this.btnOk) {
			this.okAction();
		}
	}

	@Override
	public void requestCancel(PlayerSelector source)
	{
		SwingUtil.close(this);
	}

	@Override
	public void acceptCurrentChoices(PlayerSelector source)
	{
		if (source == this.left) {
			this.right.requestFocusInWindow();
		} else {
			this.okAction();
		}
	}

	@Override
	public void selectedPlayerChanged(PlayerSelector source)
	{
		this.btnOk.setEnabled(this.left.getSelectedPlayer() != null && this.right.getSelectedPlayer() != null);
	}

	@Override
	public void selectedDatabaseChanged(PlayerSelector source)
	{
	}
}
