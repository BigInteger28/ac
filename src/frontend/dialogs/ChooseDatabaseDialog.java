package frontend.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.util.List;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import engines.Database;
import resources.DatabaseResource;
import frontend.VolatileLogger;
import frontend.components.ResourceList;
import frontend.util.SwingMsg;
import frontend.util.SwingUtil;

public class ChooseDatabaseDialog extends JDialog
{
	public static Database show(Window parentWindow, int playerNumber, List<DatabaseResource> dbList,
		String preselectedFilter)
	{
		final String title = "Select database for player " + (playerNumber + 1);

		final JDialog dialog = new JDialog(parentWindow);

		final JLabel lbl = new JLabel(title);
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		lbl.setFont(SwingUtil.deriveFont(lbl.getFont(), true, 1.2f));
		lbl.setBorder(new EmptyBorder(10, 0, 10, 0));

		final DatabaseResource[] result = { null };
		final ResourceList<DatabaseResource> list;
		list = new ResourceList<>(dbList, DatabaseResource.TYPES);
		list.setPreferredSize(new Dimension(550, 300));
		list.setSelectedIndex(0);
		final Runnable chooseListener = () -> {
			result[0] = list.getSelectedResource();
			SwingUtil.close(dialog);
		};
		final Runnable cancelListener = () -> {
			SwingUtil.close(dialog);
		};
		list.addChooseListener(chooseListener);
		list.addCancelListener(cancelListener);
		list.filter(preselectedFilter);

		final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JButton btnCancel = new JButton("Cancel");
		final JButton btnOk = new JButton("Ok");
		btnCancel.addActionListener(e -> cancelListener.run());
		btnOk.addActionListener(e -> chooseListener.run());
		pnlButtons.add(btnCancel);
		pnlButtons.add(btnOk);

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.add(lbl, BorderLayout.NORTH);
		dialog.add(list);
		dialog.add(pnlButtons, BorderLayout.SOUTH);
		dialog.setTitle(title);
		dialog.setModal(true);
		dialog.pack();
		dialog.setMinimumSize(dialog.getSize());
		dialog.setLocationRelativeTo(parentWindow);
		dialog.setVisible(true);
		dialog.dispose();

		if (result[0] == null) {
			return null;
		}

		try {
			return result[0].createDatabase();
		} catch (Exception e) {
			final String name = result[0].getName();
			VolatileLogger.logf(e, "creating database '%s'", name);
			final String _message = SwingMsg.format(e);
			final String _title = "Could not create database";
			SwingMsg.err_ok(parentWindow, _title, _message);
			return show(parentWindow, playerNumber, dbList, null);
		}
	}
}
