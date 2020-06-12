package frontend.dialogs;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.*;

import frontend.util.SwingUtil;
import resources.EngineSourceManager;
import resources.Settings;

import static javax.swing.JScrollPane.*;
import static resources.Settings.settings;

public class LocationDialog
{
	private static final String SETTING_LAST_ACCESSED_FOLDER = "locationdialog.laf";

	public static void show(Window parentWindow)
	{
		final JDialog dialog = new JDialog(parentWindow);

		final List<File> locationList = EngineSourceManager.getLocations();
		final List<ListDataListener> listDataListeners = new ArrayList<>();

		final JList<File> list = new JList<>();
		list.setModel(new ListModel<File>() {
			@Override
			public int getSize()
			{
				return locationList.size();
			}

			@Override
			public File getElementAt(int index)
			{
				return locationList.get(index);
			}

			@Override
			public void addListDataListener(ListDataListener l)
			{
				listDataListeners.add(l);
			}

			@Override
			public void removeListDataListener(ListDataListener l)
			{
				listDataListeners.remove(l);
			}
		});
		list.setCellRenderer(new DefaultListCellRenderer());
		if (!locationList.isEmpty()) {
			list.setSelectedIndex(0);
		}
		final JScrollPane scrollpane = new JScrollPane(list);
		scrollpane.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollpane.setPreferredSize(new Dimension(550, 300));
		final GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTH;
		c.fill = GridBagConstraints.HORIZONTAL;
		final JPanel pnlListControl = new JPanel(new GridBagLayout());
		final JButton btnAdd = new JButton("+");
		final JButton btnRemove = new JButton("-");
		btnAdd.addActionListener(e -> {
			final JFileChooser fc = new JFileChooser();
			final String laf = settings.getProperty(SETTING_LAST_ACCESSED_FOLDER, null);
			if (laf != null) {
				fc.setCurrentDirectory(new File(laf));
			}
			fc.setDialogTitle("Choose a directory");
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fc.setAcceptAllFileFilterUsed(false);
			if (fc.showOpenDialog(dialog) != JFileChooser.APPROVE_OPTION) {
				return;
			}
			final File file = fc.getSelectedFile();
			if (file == null) {
				return;
			}
			if (file.exists() && file.isDirectory()) {
				final File pf = file.getParentFile();
				if (pf != null) {
					settings.setProperty(SETTING_LAST_ACCESSED_FOLDER, pf.getAbsolutePath());
					Settings.save();
				}
				locationList.add(file);
				for (ListDataListener l : listDataListeners) {
					l.contentsChanged(new ListDataEvent(btnRemove, ListDataEvent.INTERVAL_REMOVED,
						locationList.size() - 1, locationList.size() - 1));
				}
			}
		});
		btnRemove.addActionListener(e -> {
			final int idx = list.getSelectedIndex();
			if (0 <= idx && idx < locationList.size()) {
				locationList.remove(idx);
				for (ListDataListener l : listDataListeners) {
					l.contentsChanged(
						new ListDataEvent(btnRemove, ListDataEvent.INTERVAL_REMOVED, idx, idx));
				}
			}
		});
		pnlListControl.add(btnAdd, c);
		c.gridy++;
		pnlListControl.add(btnRemove, c);
		c.gridy++;
		c.weighty = 1;
		pnlListControl.add(new JLabel(), c);
		final JPanel pnlMain = new JPanel(new BorderLayout());
		pnlMain.add(pnlListControl, BorderLayout.WEST);
		pnlMain.add(scrollpane);

		final JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER));
		final JButton btnCancel = new JButton("Cancel");
		final JButton btnOk = new JButton("Ok");
		btnCancel.addActionListener(e -> SwingUtil.close(dialog));
		btnOk.addActionListener(e -> {
			EngineSourceManager.setLocations(locationList);
			SwingUtil.close(dialog);
		});
		pnlButtons.add(btnCancel);
		pnlButtons.add(btnOk);

		dialog.getContentPane().setLayout(new BorderLayout());
		dialog.add(pnlMain);
		dialog.add(pnlButtons, BorderLayout.SOUTH);
		dialog.setTitle("Edit locations");
		dialog.setModal(true);
		dialog.pack();
		dialog.setMinimumSize(dialog.getSize());
		dialog.setLocationRelativeTo(parentWindow);
		dialog.setVisible(true);
		dialog.dispose();
	}
}
