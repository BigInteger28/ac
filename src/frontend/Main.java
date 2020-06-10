package frontend;

import backend.Game;
import backend.GameChangeListener;
import backend.Player;
import common.Images;
import frontend.components.BigCardsDisplay;
import frontend.dialogs.ChoosePlayerDialog;
import frontend.maincontent.*;
import frontend.util.RawStringInputStream;
import frontend.util.SwingMsg;
import resources.PlayerResource;
import resources.DatabaseResource;
import resources.EngineSourceManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static common.Constants.*;

public class Main implements FrontendController, Game.Listener
{
	public static Font monospaceFont;
	public static Image backImage;
	public static Image[] bigElementImages = new Image[5];
	public static Image[] smallElementImages = new Image[5];

	public static void main(String[] args)
	{
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		monospaceFont = new Font("Courier New", Font.PLAIN, 12);
		SwingUtilities.invokeLater(Main::new);
	}

	private JFrame frame;
	private Game game;
	private Player[] players;
	private List<GameChangeListener> listeners;
	private BigCardsDisplay bigCardsDisplay;

	public Main()
	{
		Container contentPane;
		RawStringInputStream rsis;
		JPanel middlePanel, topPanel;

		this.game = new Game(this);
		this.players = new Player[2];

		this.listeners = new ArrayList<>();
		this.frame = new JFrame("Avatar Carto Java Edition");

		middlePanel = new JPanel(new BorderLayout());
		middlePanel.add(new GamePanel(this), BorderLayout.WEST);
		middlePanel.add(AnalysisGraph.bordered(), BorderLayout.CENTER);

		topPanel = new JPanel(new BorderLayout());
		topPanel.add(new Ribbon(this), BorderLayout.NORTH);
		topPanel.add(middlePanel, BorderLayout.SOUTH);

		contentPane = frame.getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(topPanel, BorderLayout.NORTH);
		contentPane.add(this.bigCardsDisplay = new BigCardsDisplay(this.game), BorderLayout.CENTER);

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
	}

	@Override
	public void onGameStart()
	{
		for (GameChangeListener listener : this.listeners) {
			listener.onGameStart(this.game.data);
		}
		this.bigCardsDisplay.gameStateChanged();
	}

	@Override
	public void onMoveDone(int[] playerElements, int result)
	{
		for (GameChangeListener listener : this.listeners) {
			listener.onGameChange(this.game.data);
		}
		this.bigCardsDisplay.gameStateChanged();
	}

	@Override
	public void onGameEnd()
	{
		for (GameChangeListener listener : this.listeners) {
			listener.onGameEnd(this.game.data);
		}
		this.bigCardsDisplay.gameStateChanged();
	}

	@Override
	public Window getWindow()
	{
		return this.frame;
	}

	@Override
	public void startNewGame()
	{
		if (this.players[0] == null || this.players[1] == null) {
			this.startNewGameAdv();
			return;
		}
		this.game.startNewGame(this.players);
	}

	@Override
	public void startNewGameAdv()
	{
		final Player[] players = new Player[2];
		final ArrayList<PlayerResource> playerList = new ArrayList<>();
		final ArrayList<DatabaseResource> dbList = new ArrayList<>();
		EngineSourceManager.collectResources(playerList, dbList, /*includeHuman*/ true);
		for (int i = 0; i < 2; i++) {
			final Player p = ChoosePlayerDialog.show(this.frame, i, playerList, dbList,
				this.players[i] == null ? null : this.players[i].getName());
			if (p == null) {
				return;
			}
			players[i] = p;
		}
		System.arraycopy(players, 0, this.players, 0, 2);

		this.startNewGame();
	}

	@Override
	public void addGameChangeListener(GameChangeListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public void chooseElement(int player, int element)
	{
		if (this.players[player] instanceof HumanPlayer) {
			((HumanPlayer) this.players[player]).setChosenElement(element);
			this.game.update();
		}
	}

}
