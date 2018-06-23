package frontend.components;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import frontend.FrontendController;

import static javax.swing.SpringLayout.*;

public class Ribbon
{
    private static final int PADDING = 5;
    
    private final FrontendController controller;
    
    public Ribbon(FrontendController controller)
    {
        this.controller = controller;
    }
    
    public JComponent createComponent()
    {
        final JTabbedPane c = new JTabbedPane();
        c.addTab("Game", this.createGameMenu());
        return c;
    }
    
    private JPanel createGameMenu()
    {
        final SpringLayout layout = new SpringLayout();
        final JPanel pnl = new JPanel(layout);

        final JButton btnNewGame = new JButton("New game");
        btnNewGame.addActionListener(e -> this.controller.startNewGame());
        pnl.add(btnNewGame);

        final JButton btnAdvGame = new JButton("adv");
        btnAdvGame.addActionListener(e -> this.controller.startNewGameAdv());
        pnl.add(btnAdvGame);
        
        layout.putConstraint(WEST, btnNewGame, PADDING, WEST, pnl);
        layout.putConstraint(NORTH, btnNewGame, PADDING, NORTH, pnl);
        layout.putConstraint(SOUTH, pnl, PADDING, SOUTH, btnNewGame);

        layout.putConstraint(NORTH, btnAdvGame, 0, NORTH, btnNewGame);
        layout.putConstraint(WEST, btnAdvGame, 0, EAST, btnNewGame);
        layout.putConstraint(EAST, pnl, -PADDING, EAST, btnAdvGame);
        
        return pnl;
    }

}
