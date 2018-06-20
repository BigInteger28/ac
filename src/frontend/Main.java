package frontend;

import backend.Engine;
import backend.Game;
import frontend.components.GamePanel;
import frontend.components.Ribbon;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static common.Constants.*;

public class Main implements FrontendController
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
    private final Engine engine;
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

        this.updateGameState(new GameState());

        //Joris tryout
        String[] players = new String[] {"Joris", "Stockfish"};
        int[] stars = new int[] {42, 112};
        game = new Game(players, stars);
        int[] stockfishSoftware = new int[] {4, 17, 36, 12, 94, 28, 147, 12, 7};
        engine = new Engine(stockfishSoftware);

        //Speler drukt op NEW GAME (tegen engine stockfish in ons geval)
        this.startNewGame();
    }
    
    @Override
    public void startNewGame()
    {
        this.game.startNewGame();
        this.updateGameState(new GameState(this.game));
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

        /*
        //Joris tryout
        this.game.doMove(element);
        int move = game.getCurrentMove();
        int playerPreviousElement = 0;
        if (move > 0) {
            playerPreviousElement = game.getMove(0, move - 1);
        }
        this.game.doMove(this.engine.getElement(move, playerPreviousElement));
        */
        
        this.updateGameState(new GameState(this.game));
    }
    
    private void updateGameState(GameState newState)
    {
        for (GameChangeListener listener : this.listeners) {
            listener.onGameChanged(newState);
        }
    }

}
