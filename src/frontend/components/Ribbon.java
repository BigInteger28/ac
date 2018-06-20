package frontend.components;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import frontend.GameController;

import static javax.swing.SpringLayout.*;

public class Ribbon
{
    private static final int PADDING = 5;
    
    public JComponent createComponent(GameController gameController)
    {
        final JTabbedPane c = new JTabbedPane();
        c.addTab("Game", this.createGameMenu(gameController));
        return c;
    }
    
    private JPanel createGameMenu(GameController gameController)
    {
        final SpringLayout layout = new SpringLayout();
        final JPanel pnl = new JPanel(layout);

        final JButton btnNewGame = new JButton("New game");
        btnNewGame.addActionListener(e -> gameController.startNewGame());
        pnl.add(btnNewGame);
        
        layout.putConstraint(WEST, btnNewGame, PADDING, WEST, pnl);
        layout.putConstraint(NORTH, btnNewGame, PADDING, NORTH, pnl);
        layout.putConstraint(SOUTH, pnl, PADDING, SOUTH, btnNewGame);
        layout.putConstraint(EAST, pnl, -PADDING, EAST, btnNewGame);
        
        return pnl;
    }

}
