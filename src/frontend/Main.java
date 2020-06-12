package frontend;

import backend.ACMain;
import backend.Game;
import backend.Player;
import common.ErrorHandler;
import engines.FixedEngine;
import frontend.dialogs.ChoosePlayerDialog;
import frontend.dialogs.LocationDialog;
import frontend.maincontent.*;
import frontend.util.RawStringInputStream;
import frontend.util.SwingMsg;
import frontend.util.SwingUtil;
import resources.PlayerResource;
import resources.Resources;
import resources.DatabaseResource;
import resources.EngineSourceManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import static common.Constants.*;

public class Main implements
	Game.Listener,
	Runnable /*for SwingUtilities#invokeLater*/,
	ActionListener /*for button actions*/
{
	private static final Color[] BUTTONCOLORS = {
		new Color(0x5050FF), new Color(0xFF5050),
		new Color(0x508050), new Color(0xFFFF50),
		new Color(0x808080)
	};
	private static final Color[] RESULTCOLORS = {
		new Color(0xFF0000), Color.black, new Color(0x008000)
	};

	private static final char BUTTON_ID_PLAYER_CONTROL = 0;
	private static final char BUTTON_ID_NEWGAME = 1;
	private static final char BUTTON_ID_CHOOSEPLAYERS = 2;
	private static final char BUTTON_ID_UNDOMOVE = 3;
	private static final char BUTTON_ID_LOCATIONS = 4;
	private static final char BUTTON_ID_WORKINGDIR = 5;
	private static final char BUTTON_ID_SECONDOPINION = 6;

	public static File settingsDir;
	public static Image backImage;
	public static Image[] bigElementImages = new Image[5];
	public static Image[] smallElementImages = new Image[5];
	public static boolean uiReady;

	static HumanPlayer humanPlayers[] = { new HumanPlayer(), new HumanPlayer() };

	public static void main(String[] args)
	{
		ErrorHandler.handler = new ErrorHandlerFrontendImpl();

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			ErrorHandler.handler.handleException(e);
		}

		ACMain.main();

		SwingUtilities.invokeLater(Main::new);
	}

	private static Border currentBorder;
	private static void pushBorder(Border border)
	{
		if (currentBorder != null) {
			currentBorder = new CompoundBorder(currentBorder, border);
		} else {
			currentBorder = border;
		}
	}
	private static JComponent wrapWithBorder(JComponent to)
	{
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.add(to);
		wrapper.setBorder(currentBorder);
		currentBorder = null;
		return wrapper;
	}

	private JFrame frame;
	private TitledBorder player1border, player2border;
	private Game game;
	private String lastOpinionPlayerName;

	private JLabel[] p1movelabels, p2movelabels;
	private JButton[] player1buttons, player2buttons;
	private JLabel player1score, player2score;
	private JPanel bigCardsDisplay;

	private boolean updateQueued;
	private boolean updateBigCards;
	private boolean updateButtons;
	private boolean updateMoves;
	private boolean updateScore;

	public Main()
	{
		RawStringInputStream rsis;
		Container contentPane;
		GridBagConstraints gbc;
		JPanel player1controls, player2controls;
		JPanel scorePanel;
		JPanel movesPanel;
		JPanel pnl;
		JButton btn;
		JTabbedPane ribbon;

		this.game = new Game(this);
		this.game.p1 = humanPlayers[0];
		this.game.p2 = humanPlayers[1];
		this.game.startNewGame();

		this.frame = new JFrame("Avatar Carto Java Edition");

		// ribbon
		ribbon = new JTabbedPane();
		pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btn = new JButton("New game");
		btn.setName(new String(new char[] { BUTTON_ID_NEWGAME }));
		btn.addActionListener(this);
		pnl.add(btn);
		btn = new JButton("Choose players");
		btn.setName(new String(new char[] { BUTTON_ID_CHOOSEPLAYERS }));
		btn.addActionListener(this);
		pnl.add(btn);
		btn = new JButton("Undo move");
		btn.setName(new String(new char[] { BUTTON_ID_UNDOMOVE }));
		btn.addActionListener(this);
		pnl.add(btn);
		btn = new JButton("Second opinion");
		btn.setName(new String(new char[] { BUTTON_ID_SECONDOPINION }));
		btn.addActionListener(this);
		pnl.add(btn);
		ribbon.addTab("Game", pnl);
		pnl = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btn = new JButton("Engine locations");
		btn.setName(new String(new char[] { BUTTON_ID_LOCATIONS }));
		btn.addActionListener(this);
		pnl.add(btn);
		btn = new JButton("Working dir");
		btn.setName(new String(new char[] { BUTTON_ID_WORKINGDIR }));
		btn.addActionListener(this);
		pnl.add(btn);
		ribbon.addTab("Settings", pnl);

		// player controls
		player1controls = new JPanel(new GridLayout(0, 5, 5, 0));
		player2controls = new JPanel(new GridLayout(0, 5, 5, 0));
		this.player1buttons = new JButton[5];
		this.player2buttons = new JButton[5];
		for (char i = 0; i < 5; i++) {
			String text = ELEMENTS[i] + " (?)";
			btn = this.player1buttons[i] = new JButton(text);
			btn.setFocusable(false);
			btn.setName(new String(new char[] { BUTTON_ID_PLAYER_CONTROL, 0, i }));
			btn.addActionListener(this);
			pnl = new JPanel(new BorderLayout());
			pnl.setBorder(new EmptyBorder(3, 3, 3, 3));
			pnl.setBackground(BUTTONCOLORS[i]);
			pnl.add(btn);
			player1controls.add(pnl);
			btn = this.player2buttons[i] = new JButton(text);
			btn.setFocusable(false);
			btn.setName(new String(new char[] { BUTTON_ID_PLAYER_CONTROL, 1, i }));
			btn.addActionListener(this);
			pnl = new JPanel(new BorderLayout());
			pnl.setBorder(new EmptyBorder(3, 3, 3, 3));
			pnl.setBackground(BUTTONCOLORS[i]);
			pnl.add(btn);
			player2controls.add(pnl);
		}

		// moves panel
		movesPanel = new JPanel(new GridLayout(2, 9, 5, 5));
		movesPanel.setBorder(new EmptyBorder(0, 20, 0, 10));
		this.p1movelabels = new JLabel[9];
		this.p2movelabels = new JLabel[9];
		for (int i = 0; i < 9; i++) {
			movesPanel.add(p1movelabels[i] = new JLabel("?", SwingConstants.CENTER));
		}
		for (int i = 0; i < 9; i++) {
			movesPanel.add(p2movelabels[i] = new JLabel("?", SwingConstants.CENTER));
		}

		// score panel
		scorePanel = new JPanel(new GridLayout(2, 1, 0, 5));
		scorePanel.setBorder(new EmptyBorder(0, 20, 0, 10));
		scorePanel.add(this.player1score = new JLabel("?", SwingConstants.CENTER));
		scorePanel.add(this.player2score = new JLabel("?", SwingConstants.CENTER));

		// big cards display
		this.bigCardsDisplay = new BigCardsDisplay(this.game);

		// placement and layout
		(contentPane = this.frame.getContentPane()).setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;

		// ribbon
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		contentPane.add(ribbon, gbc);

		// top player buttons
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 0d;
		gbc.weighty = 0d;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.insets.top = 10;
		gbc.insets.left = 10;
		gbc.insets.bottom = 5;
		gbc.insets.right = 5;
		pushBorder(this.player1border = SwingUtil.titledBorder("Player 1"));
		pushBorder(new EmptyBorder(2, 4, 4, 4));
		contentPane.add(wrapWithBorder(player1controls), gbc);

		// game field
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.weightx = .001d;
		gbc.weighty = 0d;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets.top = 5;
		gbc.insets.left = 10;
		gbc.insets.bottom = 5;
		gbc.insets.right = 5;
		pushBorder(SwingUtil.titledBorder("Game"));
		pushBorder(new EmptyBorder(2, 9, 4, 9));
		contentPane.add(wrapWithBorder(movesPanel), gbc);

		// score field
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.weightx = 0d;
		gbc.weighty = 0d;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets.top = 5;
		gbc.insets.left = 5;
		gbc.insets.bottom = 5;
		gbc.insets.right = 5;
		pushBorder(SwingUtil.titledBorder("Score"));
		pushBorder(new EmptyBorder(2, 9, 4, 9));
		contentPane.add(wrapWithBorder(scorePanel), gbc);

		// bottom player buttons
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.weightx = 0d;
		gbc.weighty = 0d;
		gbc.gridwidth = 2;
		gbc.gridheight = 1;
		gbc.insets.top = 5;
		gbc.insets.left = 10;
		gbc.insets.bottom = 5;
		gbc.insets.right = 5;
		pushBorder(this.player2border = SwingUtil.titledBorder("Player 2"));
		pushBorder(new EmptyBorder(2, 4, 4, 4));
		contentPane.add(wrapWithBorder(player2controls), gbc);

		// analysis
		gbc.gridx = 3;
		gbc.gridy = 2;
		gbc.weightx = 1d;
		gbc.weighty = 0d;
		gbc.gridwidth = 1;
		gbc.gridheight = 3;
		gbc.insets.top = 10;
		gbc.insets.left = 5;
		gbc.insets.bottom = 5;
		gbc.insets.right = 10;
		pushBorder(SwingUtil.titledBorder("Analysis"));
		pushBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(wrapWithBorder(new AnalysisGraph()), gbc);

		// big cards
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.weightx = 1d;
		gbc.weighty = 1d;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		gbc.insets.top = 5;
		gbc.insets.left = 10;
		gbc.insets.bottom = 10;
		gbc.insets.right = 10;
		pushBorder(SwingUtil.titledBorder("Big cards"));
		pushBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.add(wrapWithBorder(this.bigCardsDisplay), gbc);

		ImageIO.setUseCache(false);
		try {
			rsis = new RawStringInputStream();
			this.frame.setIconImage(ImageIO.read(rsis.resetSetSrc(Images.ICON_128)));
			backImage = ImageIO.read(rsis.resetSetSrc(Images.CARD_BACK));
			bigElementImages[WATER] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_BIG_WATER));
			bigElementImages[FIRE] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_BIG_FIRE));
			bigElementImages[EARTH] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_BIG_EARTH));
			bigElementImages[AIR] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_BIG_AIR));
			bigElementImages[DEFENSE] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_BIG_DEFENSE));
			smallElementImages[WATER] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_SMALL_WATER));
			smallElementImages[FIRE] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_SMALL_FIRE));
			smallElementImages[EARTH] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_SMALL_EARTH));
			smallElementImages[AIR] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_SMALL_AIR));
			smallElementImages[DEFENSE] = ImageIO.read(rsis.resetSetSrc(Images.ELEMENT_SMALL_DEFENSE));
		} catch (Exception e) {
			e.printStackTrace();
			SwingMsg.err_ok(null, e.getClass().getCanonicalName(), e.toString());
		}

		this.frame.pack();
		this.frame.setLocationRelativeTo(null);
		this.frame.setMinimumSize(frame.getSize());
		this.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.frame.setVisible(true);

		uiReady = true;
	}

	private void startNewGameAdv()
	{
		Player p1, p2;
		String preChosenName;
		ArrayList<PlayerResource> playerList;
		ArrayList<DatabaseResource> dbList;

		playerList = new ArrayList<>();
		dbList = new ArrayList<>();
		EngineSourceManager.collectResources(playerList, dbList, HumanPlayer.RESOURCEINSTANCE);

		preChosenName = this.game.data.isHumanControlled(0) ? null : this.game.data.getPlayerName(0);
		p1 = ChoosePlayerDialog.show(this.frame, "player 1", playerList, dbList, preChosenName);
		if (p1 == null) {
			return;
		}
		preChosenName = this.game.data.isHumanControlled(1) ? null : this.game.data.getPlayerName(1);
		p2 = ChoosePlayerDialog.show(this.frame, "player 2", playerList, dbList, preChosenName);
		if (p2 == null) {
			return;
		}
		this.game.p1 = p1;
		this.game.p2 = p2;

		this.player1border.setTitle(p1.getName());
		this.player2border.setTitle(p2.getName());
		// repaint to update the titled borders, see bug JDK-4117141
		this.frame.getContentPane().repaint();

		this.game.startNewGame();
	}

	public void queueUpdate()
	{
		if (!this.updateQueued) {
			this.updateQueued = true;
			SwingUtilities.invokeLater(this);
		}
	}

	@Override
	public void onGameStart(Game game)
	{
		this.queueUpdate();
		this.updateBigCards = true;
		this.updateButtons = true;
		this.updateMoves = true;
		this.updateScore = true;
	}

	@Override
	public void onMoveDone(Game game)
	{
		this.queueUpdate();
		this.updateBigCards = true;
		this.updateButtons = true;
		this.updateMoves = true;
		this.updateScore = true;
	}

	/**
	 * Called from eventqueue by {@link SwingUtilities#invokeLater} calls
	 */
	@Override
	public void run()
	{
		Game.Data data;
		Container parent;

		data = this.game.data;
		this.updateQueued = false;

		if (this.updateBigCards) {
			this.updateBigCards = false;
			this.bigCardsDisplay.repaint();
		}

		if (this.updateButtons) {
			boolean p1canPlay, p2canPlay;
			int p1elementsLeft[], p2elementsLeft[];

			data = this.game.data;
			p1canPlay = !data.isPlayerReady(0) && data.isHumanControlled(0);
			p2canPlay = !data.isPlayerReady(1) && data.isHumanControlled(1);
			p1elementsLeft = data.getElementsLeft(0);
			p2elementsLeft = data.getElementsLeft(1);
			for (int i = 0; i < 5; i++) {
				this.player1buttons[i].setText(ELEMENTS[i] + " (" + p1elementsLeft[i] + ")");
				this.player1buttons[i].setVisible(p1elementsLeft[i] > 0);
				this.player1buttons[i].setEnabled(p1canPlay);
				this.player2buttons[i].setText(ELEMENTS[i] + " (" + p2elementsLeft[i] + ")");
				this.player2buttons[i].setVisible(p2elementsLeft[i] > 0);
				this.player2buttons[i].setEnabled(p2canPlay);

				// if they're not visible, they take no space and that would mess up the layout
				parent = this.player1buttons[i].getParent();
				if (!parent.isMinimumSizeSet()) {
					parent.setMinimumSize(parent.getSize());
					parent.setPreferredSize(parent.getSize());
					parent = this.player2buttons[i].getParent();
					parent.setMinimumSize(parent.getSize());
					parent.setPreferredSize(parent.getSize());
				}
			}

		}

		if (this.updateMoves) {
			int currentMove;
			int p1moves[], p2moves[];
			int moveResult;
			JLabel p1lbl, p2lbl;

			currentMove = data.getCurrentMove();
			p1moves = data.getMoves(0);
			p2moves = data.getMoves(1);
			for (int i = 0; i < 9; i++) {
				p1lbl = this.p1movelabels[i];
				p2lbl = this.p2movelabels[i];
				if (currentMove <= i) {
					p1lbl.setText("?");
					p2lbl.setText("?");
					p1lbl.setForeground(RESULTCOLORS[1]);
					p2lbl.setForeground(RESULTCOLORS[1]);
				} else {
					moveResult = data.getMoveScore(i);
					p1lbl.setText(String.valueOf(CHARELEMENTS[p1moves[i]]));
					p2lbl.setText(String.valueOf(CHARELEMENTS[p2moves[i]]));
					p1lbl.setForeground(RESULTCOLORS[1 + moveResult]);
					p2lbl.setForeground(RESULTCOLORS[1 - moveResult]);
				}
			}
		}

		if (this.updateScore) {
			int score1, score2;

			this.player1score.setText(String.valueOf(data.getScore(0)));
			this.player2score.setText(String.valueOf(data.getScore(1)));
			score1 = data.getScore(0);
			score2 = data.getScore(1);
			if (score1 > score2) {
				this.player1score.setForeground(RESULTCOLORS[2]);
				this.player2score.setForeground(RESULTCOLORS[1]);
			} else if (score1 < score2) {
				this.player1score.setForeground(RESULTCOLORS[1]);
				this.player2score.setForeground(RESULTCOLORS[2]);
			} else {
				this.player1score.setForeground(RESULTCOLORS[1]);
				this.player2score.setForeground(RESULTCOLORS[1]);
			}
		}
	}

	/**
	 * Mainly button listeners
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		char data[];
		char id;

		id = (data = ((JComponent) e.getSource()).getName().toCharArray())[0];

		switch (id) {
		case BUTTON_ID_PLAYER_CONTROL:
			HumanPlayer.chosenElement[data[1]] = data[2];
			this.game.update();
			this.updateButtons = true;
			this.queueUpdate();
			break;
		case BUTTON_ID_NEWGAME:
			this.game.startNewGame();
			break;
		case BUTTON_ID_CHOOSEPLAYERS:
			this.startNewGameAdv();
			break;
		case BUTTON_ID_UNDOMOVE:
			this.game.undoMove();
			break;
		case BUTTON_ID_LOCATIONS:
			LocationDialog.show(this.frame);
			break;
		case BUTTON_ID_WORKINGDIR:
			SwingMsg.info_ok(this.frame, "Working directory", Resources.workingdir.getAbsolutePath());
			break;
		case BUTTON_ID_SECONDOPINION:
			if (game.data.isFinished()) {
				SwingMsg.err_ok(this.frame, "Opinion", "Game is finished");
				return;
			}
			ArrayList<PlayerResource> playerList = new ArrayList<>();
			ArrayList<DatabaseResource> dbList = new ArrayList<>();
			EngineSourceManager.collectResources(playerList, dbList);
			Player p = ChoosePlayerDialog.show(this.frame, "opinion", playerList, dbList, this.lastOpinionPlayerName);
			if (p != null) {
				int p1chosenElement, p2chosenElement;
				int p1moves[], p2moves[];
				String msg;

				p1moves = this.game.data.getMoves(0);
				p2moves = this.game.data.getMoves(1);
				p1moves[this.game.data.getCurrentMove()] = -1;
				p2moves[this.game.data.getCurrentMove()] = -1;
				Game g = new Game();
				g.p1 = new FixedEngine("temp_opinion_p1", p1moves);
				g.p2 = new FixedEngine("temp_opinion_p2", p2moves);
				g.startNewGame();
				p.onGameStart(g.data, 0);
				p1chosenElement = p.doMove(0, g.data);
				p.onGameStart(g.data, 1);
				p2chosenElement = p.doMove(1, g.data);
				msg = String.format(
					"%s would play:\n for player 1: %c\n for player 2: %c",
					p.getName(),
					CHARELEMENTS[p1chosenElement],
					CHARELEMENTS[p2chosenElement]
				);
				SwingMsg.info_ok(this.frame, "Opinion of " + p.getName(), msg);
				this.lastOpinionPlayerName = p.getName();
			}
			break;
		}
	}
}
