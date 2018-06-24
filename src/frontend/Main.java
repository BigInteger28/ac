package frontend;

import backend.Game;
import backend.Player;
import frontend.dialogs.ChoosePlayerDialog;
import frontend.maincontent.MainContent;
import resources.PlayerResource;
import resources.DatabaseResource;
import resources.EngineSourceManager;

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
    private final Game game;
    private final Player[] players;
    private final List<GameChangeListener> listeners;

    public Main() {
        this.listeners = new ArrayList<>(10);
        final JFrame frame = new JFrame("Avatar Carto Java Edition");
        MainContent.addTo(frame.getContentPane(), this);
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
            final Player p = ChoosePlayerDialog.show(
                this.frame,
                i + 1,
                playerList,
                dbList,
                this.players[i] == null ? null : this.players[i].getName()
            );
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
