package frontend;

import backend.ACMain;
import backend.Game;
import backend.Player;
import common.ErrorHandler;
import frontend.dialogs.ChoosePlayerDialog;
import frontend.maincontent.*;
import frontend.util.RawStringInputStream;
import frontend.util.SwingMsg;
import frontend.util.SwingUtil;
import resources.PlayerResource;
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
	FrontendController,
	Game.Listener,
	Runnable /*for SwingUtilities#invokeLater*/,
	ActionListener /*for buttons*/
{
	private static final char BUTTON_ID_PLAYER_CONTROL = 0;

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

	private JButton[] player1buttons, player2buttons;
	private MovesPanel movesPanel;
	private ScorePanel scorePanel;
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
		JPanel pnl;
		JButton btn;

		(this.game = new Game()).addListener(this);
		this.game.p1 = humanPlayers[0];
		this.game.p2 = humanPlayers[1];
		this.game.startNewGame();

		this.frame = new JFrame("Avatar Carto Java Edition");

		// player controls
		player1controls = new JPanel(new GridLayout(0, 5, 5, 0));
		player2controls = new JPanel(new GridLayout(0, 5, 5, 0));
		this.player1buttons = new JButton[5];
		this.player2buttons = new JButton[5];
		for (char i = 0; i < 5; i++) {
			String text = STANDARDELEMENTS[i] + " (?)";
			btn = this.player1buttons[i] = new JButton(text);
			btn.setFocusable(false);
			btn.setName(new String(new char[] { BUTTON_ID_PLAYER_CONTROL, 0, i }));
			btn.addActionListener(this);
			pnl = new JPanel(new BorderLayout());
			pnl.setBorder(new EmptyBorder(3, 3, 3, 3));
			pnl.setBackground(new Color(BUTTONCOLORS[i]));
			pnl.add(btn);
			player1controls.add(pnl);
			btn = this.player2buttons[i] = new JButton(text);
			btn.setFocusable(false);
			btn.setName(new String(new char[] { BUTTON_ID_PLAYER_CONTROL, 1, i }));
			btn.addActionListener(this);
			pnl = new JPanel(new BorderLayout());
			pnl.setBorder(new EmptyBorder(3, 3, 3, 3));
			pnl.setBackground(new Color(BUTTONCOLORS[i]));
			pnl.add(btn);
			player2controls.add(pnl);
		}

		// placement and layout
		(contentPane = this.frame.getContentPane()).setLayout(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;

		// ribbon
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.weightx = 1d;
		gbc.gridwidth = 3;
		gbc.gridheight = 1;
		contentPane.add(new Ribbon(this), gbc);

		// top player buttons
		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.weightx = 0d;
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
		this.movesPanel = new MovesPanel();
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.weightx = .001d;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets.top = 5;
		gbc.insets.left = 10;
		gbc.insets.bottom = 5;
		gbc.insets.right = 5;
		pushBorder(SwingUtil.titledBorder("Game"));
		pushBorder(new EmptyBorder(2, 9, 4, 9));
		contentPane.add(wrapWithBorder(this.movesPanel), gbc);

		// score field
		this.scorePanel = new ScorePanel();
		gbc.gridx = 2;
		gbc.gridy = 3;
		gbc.weightx = 0d;
		gbc.gridwidth = 1;
		gbc.gridheight = 1;
		gbc.insets.top = 5;
		gbc.insets.left = 5;
		gbc.insets.bottom = 5;
		gbc.insets.right = 5;
		pushBorder(SwingUtil.titledBorder("Score"));
		pushBorder(new EmptyBorder(2, 9, 4, 9));
		contentPane.add(wrapWithBorder(this.scorePanel), gbc);

		// bottom player buttons
		gbc.gridx = 1;
		gbc.gridy = 4;
		gbc.weightx = 0d;
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
		this.bigCardsDisplay = new BigCardsDisplay(this.game);
		gbc.gridx = 1;
		gbc.gridy = 5;
		gbc.weightx = 1d;
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

	@Override
	public Window getWindow()
	{
		return this.frame;
	}

	@Override
	public void startNewGame()
	{
		this.game.startNewGame();
	}

	@Override
	public void startNewGameAdv()
	{
		Player p1, p2;
		String preChosenName;
		ArrayList<PlayerResource> playerList;
		ArrayList<DatabaseResource> dbList;

		playerList = new ArrayList<>();
		dbList = new ArrayList<>();
		EngineSourceManager.collectResources(playerList, dbList, HumanPlayer.RESOURCEINSTANCE);

		preChosenName = this.game.data.isHumanControlled(0) ? null : this.game.data.getPlayerName(0);
		p1 = ChoosePlayerDialog.show(this.frame, 0, playerList, dbList, preChosenName);
		if (p1 == null) {
			return;
		}
		preChosenName = this.game.data.isHumanControlled(1) ? null : this.game.data.getPlayerName(1);
		p2 = ChoosePlayerDialog.show(this.frame, 1, playerList, dbList, preChosenName);
		if (p2 == null) {
			return;
		}
		this.game.p1 = p1;
		this.game.p2 = p2;

		this.player1border.setTitle(p1.getName());
		this.player2border.setTitle(p2.getName());
		// repaint to update the titled borders, see bug JDK-4117141
		this.frame.getContentPane().repaint();

		this.startNewGame();
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
	public void onMoveDone(Game game, int[] playerElements, int result)
	{
		this.queueUpdate();
		this.updateBigCards = true;
		this.updateButtons = true;
		this.updateMoves = true;
		this.updateScore = true;
	}

	@Override
	public void onGameEnd(Game game)
	{
	}

	private void updatePlayerButtons()
	{
		boolean p1canPlay, p2canPlay;
		int p1elementsLeft[], p2elementsLeft[];
		Game.Data data;

		data = this.game.data;
		p1canPlay = !data.isPlayerReady(0) && data.isHumanControlled(0);
		p2canPlay = !data.isPlayerReady(1) && data.isHumanControlled(1);
		p1elementsLeft = data.getElementsLeft(0);
		p2elementsLeft = data.getElementsLeft(1);
		for (int i = 0; i < 5; i++) {
			this.player1buttons[i].setText(STANDARDELEMENTS[i] + " (" + p1elementsLeft[i] + ")");
			this.player1buttons[i].setEnabled(p1canPlay && p1elementsLeft[i] > 0);
			this.player2buttons[i].setText(STANDARDELEMENTS[i] + " (" + p2elementsLeft[i] + ")");
			this.player2buttons[i].setEnabled(p2canPlay && p2elementsLeft[i] > 0);
		}
	}

	/**
	 * Called from eventqueue by {@link SwingUtilities#invokeLater} calls
	 */
	@Override
	public void run()
	{
		this.updateQueued = false;

		if (this.updateBigCards) {
			this.updateBigCards = false;
			this.bigCardsDisplay.repaint();
		}

		if (this.updateButtons) {
			this.updatePlayerButtons();
		}

		if (this.updateMoves) {
			this.movesPanel.updateLabels(this.game.data);
		}

		if (this.updateScore) {
			this.scorePanel.updateLabels(this.game.data.getScore(0), this.game.data.getScore(1));
		}
	}

	/**
	 * Mainly button listeners
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		char data[];

		data = ((JComponent) e.getSource()).getName().toCharArray();

		if (data[0] == BUTTON_ID_PLAYER_CONTROL) {
			humanPlayers[data[1]].setChosenElement(data[2]);
			this.game.update();
			this.updateButtons = true;
			this.queueUpdate();
		}
	}
}
