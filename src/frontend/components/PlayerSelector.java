package frontend.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import backend.Database;
import backend.Player;
import backend.Resource;
import backend.ResourceType;
import resources.EngineSourceManager;

public class PlayerSelector extends JPanel implements
	KeyListener /*for the textfields*/,
	ListSelectionListener,
	ListCellRenderer<Resource> /*for the list*/,
	Runnable /*for SwingUtilities#invokeLater events*/
{
	private JList<Player> lstPlayer;
	private HideableList<Database> lstDatabase;
	private ArrayList<Player> players;
	private ArrayList<Database> databases;
	private JTextField txtFilterPlayer;
	private JTextField txtFilterDatabase;
	private JLabel[] lblPlayerTypes;
	private JLabel[] lblDatabaseTypes;

	private JPanel lstCellPnl;
	private JPanel lstCellPnlTypeColorSquare;
	private JLabel lstCellLblName;
	private JLabel lstCellLblLocation;

	private boolean updateQueued;
	private boolean needPlayerFilterUpdate;
	private boolean needDatabaseFilterUpdate;

	public Listener listener;

	public PlayerSelector(
		ArrayList<Player> players, ArrayList<Database> databases,
		Player preselectedPlayer, Database preselectedDatabase)
	{
		super(new GridBagLayout());

		JScrollPane playerScroller, databaseScroller;
		GridBagConstraints gbc;
		JLabel lblPlayer, lblDatabase;
		JPanel playerListInfo, databaseListInfo;
		JPanel pnl;

		this.players = players;
		this.databases = databases;

		// the panel for list cell rendering
		this.lstCellPnlTypeColorSquare = new JPanel();
		this.lstCellPnlTypeColorSquare.setPreferredSize(new Dimension(10, 10));
		this.lstCellPnlTypeColorSquare.setOpaque(true);
		this.lstCellLblName = new JLabel();
		this.lstCellLblName.setBorder(new EmptyBorder(0, 5, 0, 5));
		this.lstCellLblLocation = new JLabel();
		this.lstCellLblLocation.setForeground(new Color(0x666666));
		this.lstCellLblLocation.setBorder(new EmptyBorder(0, 5, 0, 5));
		this.lstCellPnl = new JPanel(new BorderLayout());
		this.lstCellPnl.setBorder(new MatteBorder(0, 0, 1, 0, new Color(0xEEEEEE)));
		this.lstCellPnl.setOpaque(true);
		this.lstCellPnl.add(this.lstCellPnlTypeColorSquare, BorderLayout.WEST);
		this.lstCellPnl.add(this.lstCellLblName);
		this.lstCellPnl.add(this.lstCellLblLocation, BorderLayout.EAST);

		// player checkbox
		lblPlayer = new JLabel("Player:");

		// player filter
		txtFilterPlayer = new JTextFieldWithPlaceholder("player filter");
		txtFilterPlayer.addKeyListener(this);

		// player list
		this.lstPlayer = new JList<>(new DefaultListModel<Player>());
		this.lstPlayer.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.lstPlayer.setCellRenderer(this);
		this.lstPlayer.setFocusable(false);
		this.lstPlayer.addListSelectionListener(this);
		playerScroller = new JScrollPane(this.lstPlayer);
		playerScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		playerScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

		// player list info
		this.lblPlayerTypes = new JLabel[ResourceType.PLAYERTYPES.length];
		playerListInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
		for (int i = 0; i < ResourceType.PLAYERTYPES.length; i++) {
			pnl = new JPanel();
			pnl.setOpaque(true);
			pnl.setBackground(new Color(ResourceType.PLAYERTYPES[i].color));
			pnl.setPreferredSize(new Dimension(10, 10));
			playerListInfo.add(pnl);
			playerListInfo.add(new JLabel(ResourceType.PLAYERTYPES[i].name));
			playerListInfo.add(this.lblPlayerTypes[i] = new JLabel());
		}

		// database checkbox
		lblDatabase = new JLabel("Database:");

		// database filter
		txtFilterDatabase = new JTextFieldWithPlaceholder("database filter");
		txtFilterDatabase.addKeyListener(this);

		// database list
		this.lstDatabase = new HideableList<>(new DefaultListModel<Database>());
		this.lstDatabase.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.lstDatabase.setCellRenderer(this);
		this.lstDatabase.setFocusable(false);
		this.lstDatabase.addListSelectionListener(this);
		databaseScroller = new JScrollPane(this.lstDatabase);
		databaseScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		databaseScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		databaseScroller.setPreferredSize(new Dimension(20, 100));

		// datbase list info
		this.lblDatabaseTypes = new JLabel[ResourceType.DATABASETYPES.length];
		databaseListInfo = new JPanel(new FlowLayout(FlowLayout.CENTER));
		for (int i = 0; i < ResourceType.DATABASETYPES.length; i++) {
			pnl = new JPanel();
			pnl.setOpaque(true);
			pnl.setBackground(new Color(ResourceType.DATABASETYPES[i].color));
			pnl.setPreferredSize(new Dimension(10, 10));
			databaseListInfo.add(pnl);
			databaseListInfo.add(new JLabel(ResourceType.DATABASETYPES[i].name));
			databaseListInfo.add(this.lblDatabaseTypes[i] = new JLabel());
		}

		// placement and layout
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;

		// player label
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		this.add(lblPlayer, gbc);

		// player filter
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		this.add(this.txtFilterPlayer, gbc);

		// player list
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.weightx = 1d;
		gbc.weighty = 1d;
		this.add(playerScroller, gbc);

		// player list info
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		this.add(playerListInfo, gbc);

		// database label
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		this.add(lblDatabase, gbc);

		// database filter
		gbc.gridx = 1;
		gbc.gridy = 6;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		this.add(this.txtFilterDatabase, gbc);

		// database list
		gbc.gridx = 1;
		gbc.gridy = 7;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		this.add(databaseScroller, gbc);

		// database list info
		gbc.gridx = 1;
		gbc.gridy = 8;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		this.add(databaseListInfo, gbc);

		this.setPreferredSize(new Dimension(450, 500));

		if (preselectedPlayer != null && !"Human".equals(preselectedPlayer.getName())) {
			this.txtFilterPlayer.setText(preselectedPlayer.getName());
		}
		if (preselectedDatabase != null) {
			this.txtFilterDatabase.setText(preselectedDatabase.getName());
		}
		this.updateFilter(this.lstPlayer, this.players, this.txtFilterPlayer.getText(), this.lblPlayerTypes);
		this.updateFilter(this.lstDatabase, this.databases, this.txtFilterDatabase.getText(), this.lblDatabaseTypes);
		this.lstPlayer.setSelectedValue(preselectedPlayer, true);
		this.lstDatabase.setSelectedValue(preselectedDatabase, true);
	}

	@Override
	public boolean requestFocusInWindow()
	{
		this.txtFilterPlayer.selectAll();
		return this.txtFilterPlayer.requestFocusInWindow();
	}

	public Player getSelectedPlayer()
	{
		return this.lstPlayer.getSelectedValue();
	}

	public Database getSelectedDatabase()
	{
		return this.lstDatabase.getSelectedValue();
	}

	private void queueUpdate()
	{
		if (!this.updateQueued) {
			this.updateQueued = true;
			SwingUtilities.invokeLater(this);
		}
	}

	@Override
	public void run()
	{
		this.updateQueued = false;

		if (this.needPlayerFilterUpdate) {
			this.needPlayerFilterUpdate = false;
			this.updateFilter(this.lstPlayer, this.players, this.txtFilterPlayer.getText(), this.lblPlayerTypes);
			if (this.txtFilterDatabase.isEnabled()) {
				this.txtFilterDatabase.setText(this.txtFilterPlayer.getText());
				this.needDatabaseFilterUpdate = true;
			}
		}

		if (this.needDatabaseFilterUpdate) {
			this.needDatabaseFilterUpdate = false;
			this.updateFilter(this.lstDatabase, this.databases, this.txtFilterDatabase.getText(), this.lblDatabaseTypes);
		}
	}

	private <T extends Resource> void updateFilter(JList<T> list, ArrayList<T> allValues, String filterValue, JLabel[] typeLabels)
	{
		boolean doFilter;
		DefaultListModel<T> model;
		T val;
		T nextExistingValue;
		int nextExistingIndex;
		int nextAllIndex;
		int typeCounts[];

		typeCounts = new int[typeLabels.length];
		filterValue = filterValue.toLowerCase();
		doFilter = !filterValue.isEmpty();
		model = (DefaultListModel<T>) list.getModel();

		// first remove all the elements that should be gone
		if (doFilter) {
			for (int i = 0; i < model.getSize(); i++) {
				val = model.get(i);
				if (!val.getName().toLowerCase().contains(filterValue)) {
					model.remove(i);
					i--;
				}
			}
		}

		// then add the missing ones
		nextExistingIndex = 0;
		nextAllIndex = 0;
		while (nextAllIndex < allValues.size()) {
			nextExistingValue = null;
			if (nextExistingIndex < model.getSize()) {
				nextExistingValue = model.get(nextExistingIndex);
			}
			val = allValues.get(nextAllIndex);
			if (!doFilter || val.getName().toLowerCase().contains(filterValue)) {
				if (val == nextExistingValue) {
					;
				} else if (nextExistingValue == null) {
					model.addElement(val);
				} else {
					model.add(nextExistingIndex, val);
				}
				nextExistingIndex++;
			}
			nextAllIndex++;
		}

		for (int i = 0; i < model.getSize(); i++) {
			typeCounts[model.get(i).getType().index]++;
		}
		for (int i = 0; i < typeLabels.length; i++) {
			typeLabels[i].setText("(" + typeCounts[i] + ')');
		}

		if (list.getSelectedIndex() != -1) {
			list.ensureIndexIsVisible(list.getSelectedIndex());
		}
		if (list.getSelectedValue() == null && !model.isEmpty()) {
			list.setSelectedIndex(0);
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		if (e.getSource() == this.txtFilterPlayer) {
			switch (e.getKeyChar()) {
			case '\n':
			case '\r':
				if (this.lstPlayer.getSelectedValue() == null) {
					return;
				}
				if (this.txtFilterDatabase.isEnabled()) {
					this.txtFilterDatabase.requestFocusInWindow();
					this.txtFilterDatabase.selectAll();
				} else {
					if (this.listener != null) {
						this.listener.acceptCurrentChoices(this);
					}
				}
				break;
			case 27:
				if (this.txtFilterPlayer.getText().isEmpty()) {
					if (this.listener != null) {
						this.listener.requestCancel(this);
					}
				} else {
					this.txtFilterPlayer.setText("");
				}
				break;
			default:
				this.needPlayerFilterUpdate = true;
				this.queueUpdate();
				break;
			}
			return;
		}

		if (e.getSource() == this.txtFilterDatabase) {
			switch (e.getKeyChar()) {
			case '\n':
			case '\r':
				if (this.listener != null) {
					this.listener.acceptCurrentChoices(this);
				}
				break;
			case 27:
				if (this.txtFilterDatabase.getText().isEmpty()) {
					if (this.listener != null) {
						this.listener.requestCancel(this);
					}
				} else {
					this.txtFilterDatabase.setText("");
				}
				break;
			default:
				this.needDatabaseFilterUpdate = true;
				this.queueUpdate();
				break;
			}
			return;
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (e.getSource() == this.txtFilterPlayer) {
			if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
				this.lstPlayer.dispatchEvent(e);
			}
			return;
		}

		if (e.getSource() == this.txtFilterDatabase) {
			if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_DOWN) {
				this.lstDatabase.dispatchEvent(e);
			}
			return;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}

	@Override
	public void valueChanged(ListSelectionEvent e)
	{
		if (e.getSource() == this.lstPlayer) {
			if (this.listener != null) {
				this.listener.selectedPlayerChanged(this);
			}

			if (this.lstPlayer.getSelectedValue() == null) {
				this.txtFilterDatabase.setEnabled(false);
				this.lstDatabase.setEnabled(false);
				this.lstDatabase.setSelectedIndex(-1);
				this.lstDatabase.hideWithMessage("No player chosen");
			} else if (this.lstPlayer.getSelectedValue().canUseDatabase()) {
				this.txtFilterDatabase.setEnabled(true);
				this.lstDatabase.setEnabled(true);
				this.lstDatabase.hideWithMessage(null);
				this.txtFilterDatabase.setText(this.lstPlayer.getSelectedValue().getName());
				this.needDatabaseFilterUpdate = true;
				this.queueUpdate();
			} else {
				this.txtFilterDatabase.setEnabled(false);
				this.lstDatabase.setEnabled(false);
				this.lstDatabase.setSelectedIndex(-1);
				this.lstDatabase.hideWithMessage("This player cannot use a db");
			}
			return;
		}

		if (e.getSource() == this.lstDatabase) {
			if (this.listener != null) {
				this.listener.selectedDatabaseChanged(this);
			}
		}
	}

	@Override
	public Component getListCellRendererComponent(
		JList<? extends Resource> list,
		Resource value,
		int index,
		boolean isSelected,
		boolean cellHasFocus)
	{
		String path;
		List<File> locations;

		if (isSelected) {
			this.lstCellPnl.setBackground(list.getSelectionBackground());
			this.lstCellPnl.setForeground(list.getSelectionForeground());
		} else {
			this.lstCellPnl.setBackground(list.getBackground());
			this.lstCellPnl.setForeground(list.getForeground());
		}

		this.lstCellPnlTypeColorSquare.setBackground(new Color(value.getType().color));
		this.lstCellLblName.setText(value.getName());

		if ((path = value.getPath()) != null) {
			this.lstCellLblLocation.setVisible(true);
			this.lstCellLblLocation.setText(path);
			locations = EngineSourceManager.getLocations();
			for (File location : locations) {
				String locationPath = location.getAbsolutePath();
				if (path.startsWith(locationPath)) {
					final int len = locationPath.length();
					if (path.length() > len &&
						(path.charAt(len) == '/' || path.charAt(len) == '\\'))
					{
						this.lstCellLblLocation.setText(path.substring(len + 1));
					} else {
						this.lstCellLblLocation.setText(path.substring(len));
					}
					break;
				}
			}
		} else {
			this.lstCellLblLocation.setVisible(false);
		}

		return this.lstCellPnl;
	}

	public interface Listener
	{
		void requestCancel(PlayerSelector source);
		void acceptCurrentChoices(PlayerSelector source);
		void selectedPlayerChanged(PlayerSelector source);
		void selectedDatabaseChanged(PlayerSelector source);
	}
}
