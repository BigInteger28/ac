package frontend;

import backend.Game;
import backend.Game.Data;
import backend.Player;
import frontend.components.GamePanel;
import frontend.components.Ribbon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static common.Constants.*;

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

    private final GamePanel gamePanel;
    private final Ribbon ribbon;
    private final Game game;
    private final HumanPlayer[] humanPlayers;
    private final List<GameChangeListener> listeners;

    public Main() {
        this.listeners = new ArrayList<>(10);
        this.gamePanel = new GamePanel(this);
        this.ribbon = new Ribbon();
        final JPanel content = new JPanel(new BorderLayout());
        content.add(this.ribbon.createComponent(this), BorderLayout.NORTH);
        content.add(this.gamePanel);
        final JFrame frame = new JFrame("Avatar Carto Java Edition");
        frame.setContentPane(content);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(frame.getSize());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        this.game = new Game(this);
        this.humanPlayers = new HumanPlayer[] {
            new HumanPlayer(1), new HumanPlayer(2)
        };
        
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
        this.notifyGameListeners();
    }

    @Override
    public void onMoveDone(int[] playerElements, int result)
    {
        this.notifyGameListeners();
    }

    @Override
    public void onGameEnd()
    {
    }
    
    @Override
    public void startNewGame()
    {
        this.game.startNewGame(this.humanPlayers[0], this.humanPlayers[1]);
    }

    @Override
    public void addGameChangeListener(GameChangeListener listener)
    {
        this.listeners.add(listener);
    }

    @Override
    public void chooseElement(int player, int element)
    {
        System.out.printf(
            "player %d chose element %d (%s)%n",
            player,
            element,
            STANDARDELEMENTS[element]
        );

        this.humanPlayers[player].chosenElement = element;
        this.game.update();
    }
    
    private void notifyGameListeners()
    {
        for (GameChangeListener listener : this.listeners) {
            listener.onGameChanged(this.game.getData());
        }
    }
    
    private class HumanPlayer implements Player
    {
        private final int p;
        private int chosenElement;
        public HumanPlayer(int p)
        {
            this.p = p;
        }
        @Override
        public String getName()
        {
            return "Human " + this.p;
        }
        @Override
        public int doMove(Data gamedata)
        {
            return this.chosenElement;
        }
        @Override
        public void onGameStart(Game.Data data, int yourPlayerNumber)
        {
            this.chosenElement = -1;
        }
        @Override
        public void onMoveDone(int yourMove, int otherMove, int score)
        {
            this.chosenElement = -1;
        }
        @Override
        public void onGameEnd(Game.Data data)
        {
        }
        @Override
        public boolean isHumanControlled()
        {
            return true;
        }
    }

}
