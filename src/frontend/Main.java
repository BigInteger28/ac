package frontend;

import backend.ACMain;
import backend.Game;
import backend.Player;
import common.ErrorHandler;
import common.Images;
import frontend.dialogs.ChoosePlayerDialog;
import frontend.maincontent.*;
import frontend.util.RawStringInputStream;
import frontend.util.SwingMsg;
import frontend.util.SwingUtil;
import resources.PlayerResource;
import resources.DatabaseResource;
import resources.EngineSourceManager;
import resources.HumanPlayerResource;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

import static common.Constants.*;

public class Main implements
	FrontendController,
	Game.Listener,
	Runnable /*for SwingUtilities#invokeLater*/
{
	public static File settingsDir;
	public static Image backImage;
	public static Image[] bigElementImages = new Image[5];
	public static Image[] smallElementImages = new Image[5];
	public static boolean uiReady;

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

	private PlayerControls player1controls, player2controls;
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

		(this.game = new Game()).addListener(this);
		this.game.p1 = HumanPlayerResource.INSTANCE.createPlayer(0);
		this.game.p2 = HumanPlayerResource.INSTANCE.createPlayer(1);
		this.game.startNewGame();

		this.frame = new JFrame("Avatar Carto Java Edition");
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
		this.player1controls = new PlayerControls(this, 0);
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
		contentPane.add(wrapWithBorder(this.player1controls), gbc);

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
		this.player2controls = new PlayerControls(this, 1);
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
		contentPane.add(wrapWithBorder(this.player2controls), gbc);

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
		EngineSourceManager.collectResources(playerList, dbList, /*includeHuman*/ true);

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
	public void chooseElement(int player, int element)
	{
		// TODO this is shit
		// TODO try to remove some of the Resource shit?
		// resources are created with player number, why not only in game start?
		if (player == 0) {
			if (this.game.p1 instanceof HumanPlayer) {
				((HumanPlayer) this.game.p1).setChosenElement(element);
				this.game.update();
				this.updateButtons = true;
				this.queueUpdate();
			}
		} else {
			if (this.game.p2 instanceof HumanPlayer) {
				((HumanPlayer) this.game.p2).setChosenElement(element);
				this.game.update();
				this.updateButtons = true;
				this.queueUpdate();
			}
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
			this.player1controls.updateButtons(this.game.data, 0);
			this.player2controls.updateButtons(this.game.data, 1);
		}

		if (this.updateMoves) {
			this.movesPanel.updateLabels(this.game.data);
		}

		if (this.updateScore) {
			this.scorePanel.updateLabels(this.game.data.getScore(0), this.game.data.getScore(1));
		}
	}
}
