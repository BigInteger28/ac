package frontend.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.*;

import backend.Player;
import resources.DatabaseResource;
import resources.PlayerResource;
import frontend.VolatileLogger;
import frontend.components.ResourceList;
import frontend.util.SwingMsg;
import frontend.util.SwingUtil;

public class ChoosePlayerDialog extends JDialog
{
	public static Player show(Window parentWindow, String title, List<PlayerResource> playerList, List<DatabaseResource> dbList, String preselectedPlayer)
	{
		final JDialog dialog = new JDialog(parentWindow);

		final PlayerResource[] result = { null };
		final ResourceList<PlayerResource> list;
		list = new ResourceList<>(playerList, PlayerResource.TYPES);
		list.setPreferredSize(new Dimension(550, 300));
		if (!list.setSelectedResource(preselectedPlayer)) {
			list.setSelectedResource('<' + preselectedPlayer + '>');
		}
		final Runnable chooseListener = () -> {
			result[0] = list.getSelectedResource();
			SwingUtil.close(dialog);
		};
		final Runnable cancelListener = () -> {
			SwingUtil.close(dialog);
		};
		list.addChooseListener(chooseListener);
		list.addCancelListener(cancelListener);

		final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JButton btnCancel = new JButton("Cancel");
		final JButton btnOk = new JButton("Ok");
		btnCancel.addActionListener(e -> cancelListener.run());
		btnOk.addActionListener(e -> chooseListener.run());
		pnlButtons.add(btnCancel);
		pnlButtons.add(btnOk);

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.add(list);
		dialog.add(pnlButtons, BorderLayout.SOUTH);
		dialog.setTitle("Select player for " + title);
		dialog.setModal(true);
		dialog.pack();
		dialog.setMinimumSize(dialog.getSize());
		dialog.setLocationRelativeTo(parentWindow);
		dialog.setVisible(true);
		dialog.dispose();

		if (result[0] == null) {
			return null;
		}

		final Player player;
		try {
			player = result[0].createPlayer();
		} catch (Exception e) {
			final String name = result[0].getName();
			VolatileLogger.logf(e, "creating player '%s'", name);
			final String _message = SwingMsg.format(e);
			final String _title = "Could not create player";
			SwingMsg.err_ok(parentWindow, _title, _message);
			return show(parentWindow, title, playerList, dbList, name);
		}

		if (player.canUseDatabase()) {
			final String fileName = player.getName();
			final int lidx = fileName.lastIndexOf('.');
			final String playerName;
			if (lidx != -1) {
				playerName = fileName.substring(0, lidx);
			} else {
				playerName = fileName;
			}

			player.useDatabase(ChooseDatabaseDialog.show(parentWindow, title, dbList, playerName));
		}

		return player;
	}
}
