package frontend;

import backend.Game;
import backend.Player;
import frontend.components.GamePanel;
import frontend.components.Ribbon;
import frontend.dialogs.ChoosePlayerDialog;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Main implements FrontendController, Game.Listener
{

    public static Font monospaceFont;

    public static void main(String[] args) {
        //AFBLIJVEN, ROBIN WAAKT HIER!!!

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        monospaceFont = new Font("Courier New", Font.PLAIN, 12);
        SwingUtilities.invokeLater(Main::new);
    }

    private final JFrame frame;
    private final GamePanel gamePanel;
    private final Game game;
    private final Player[] players;
    private final List<GameChangeListener> listeners;

    public Main() {
        this.listeners = new ArrayList<>(10);
        this.gamePanel = new GamePanel(this);
        final JPanel content = new JPanel(new BorderLayout());
        content.add(new Ribbon(this).createComponent(), BorderLayout.NORTH);
        content.add(this.gamePanel);
        final JFrame frame = new JFrame("Avatar Carto Java Edition");
        frame.setContentPane(content);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(frame.getSize());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        this.frame = frame;

        this.game = new Game(this);
        this.players = new Player[2];
        
        /*
        //Joris tryout
        String[] players = new String[] {"Joris", "Stockfish"};
        int[] stars = new int[] {42, 112};
        game = new Game(players, stars);
        int[] stockfishSoftware = new int[] {4, 17, 36, 12, 94, 28, 147, 12, 7};
        engine = new Engine(stockfishSoftware);

        //Speler drukt op NEW GAME (tegen engine stockfish in ons geval)
        this.startNewGame();
        */
    }

    @Override
    public void onGameStart()
    {
        for (GameChangeListener listener : this.listeners) {
            listener.onGameStart(this.game.getData());
        }
    }

    @Override
    public void onMoveDone(int[] playerElements, int result)
    {
        for (GameChangeListener listener : this.listeners) {
            listener.onGameChange(this.game.getData());
        }
    }

    @Override
    public void onGameEnd()
    {
        for (GameChangeListener listener : this.listeners) {
            listener.onGameEnd(this.game.getData());
        }
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
        for (int i = 0; i < 2; i++) {
            final Player p = ChoosePlayerDialog.choosePlayer(this.frame, i + 1);
            if (p == null) {
                return;
            }
            this.players[i] = p;
        }

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
