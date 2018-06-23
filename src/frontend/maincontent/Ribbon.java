package frontend.maincontent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import frontend.FrontendController;
import frontend.dialogs.LocationDialog;

class Ribbon extends JTabbedPane
{
    private static final String N = SpringLayout.NORTH;
    private static final String E = SpringLayout.EAST;
    private static final String S = SpringLayout.SOUTH;
    private static final String W = SpringLayout.WEST;
    private static final int PADDING = 5;
    
    private final FrontendController controller;
    
    Ribbon(FrontendController controller)
    {
        this.controller = controller;
        this.addTab("Game", this.createGameMenu());
        this.addTab("Settings", this.createSettingsMenu());
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
        
        layout.putConstraint(W, btnNewGame, PADDING, W, pnl);
        layout.putConstraint(N, btnNewGame, PADDING, N, pnl);
        layout.putConstraint(S, pnl, PADDING, S, btnNewGame);

        layout.putConstraint(N, btnAdvGame, 0, N, btnNewGame);
        layout.putConstraint(W, btnAdvGame, 0, E, btnNewGame);
        layout.putConstraint(E, pnl, -PADDING, E, btnAdvGame);
        
        return pnl;
    }

    private JPanel createSettingsMenu()
    {
        final SpringLayout layout = new SpringLayout();
        final JPanel pnl = new JPanel(layout);

        final JButton btnLocations = new JButton("Engine locations");
        btnLocations.addActionListener(e -> LocationDialog.show(this.controller));
        pnl.add(btnLocations);
        
        layout.putConstraint(W, btnLocations, PADDING, W, pnl);
        layout.putConstraint(N, btnLocations, PADDING, N, pnl);
        layout.putConstraint(S, pnl, PADDING, S, btnLocations);
        layout.putConstraint(E, pnl, -PADDING, E, btnLocations);
        
        return pnl;
    }
    
}
