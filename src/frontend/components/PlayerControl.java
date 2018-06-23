package frontend.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import backend.Game;
import backend.Game.Data;
import frontend.FrontendController;
import frontend.GameChangeListener;

import java.awt.*;

import static common.Constants.STANDARDELEMENTS;

class PlayerControl extends JPanel implements GameChangeListener
{
    private static final int[] BUTTONCOLORS = {
        0xFFFF00, 0x008000, 0xFF0000, 0x0000FF, 0x808080
    };

    private final TitledBorder titleBorder;
    private final JButton[] buttons;
    private final int player;

    PlayerControl(
        String name,
        int player,
        FrontendController controller)
    {
        this.player = player;
        this.setLayout(new GridLayout(0, 5, 5, 0));
        this.titleBorder = new DefaultTitledBorder(name);
        final Border innerBorder = new EmptyBorder(2, 4, 4, 4);
        this.setBorder(new CompoundBorder(titleBorder, innerBorder));
        this.buttons = new JButton[5];

        for (int i = 0; i < 5; i++) {
            final JButton button = new JButton(STANDARDELEMENTS[i] + " (?)");
            button.setBackground(new Color(BUTTONCOLORS[i]));
            button.setFocusable(false);
            this.add(button);
            final int element = i;
            button.addActionListener(e -> {
                for (JButton b : this.buttons) {
                    b.setEnabled(false);
                }
                controller.chooseElement(player, element);
            });
            button.setText(STANDARDELEMENTS[i] + " (0)");
            button.setEnabled(false);
            this.buttons[i] = button;
        }

        controller.addGameChangeListener(this);
    }
    
    private void updateButtons(Game.Data data)
    {
        for (int i = 0; i < 5; i++) {
            final int elementsLeft = data.getElementsLeft(this.player, i);
            final boolean ishuman = data.isHumanControlled(this.player);
            this.buttons[i].setText(STANDARDELEMENTS[i] + " (" + elementsLeft + ")");
            this.buttons[i].setEnabled(ishuman && elementsLeft > 0);
        }
    }

    @Override
    public void onGameStart(Data data)
    {
        this.updateButtons(data);
        this.titleBorder.setTitle(data.getPlayerName(this.player));
        this.repaint(); // see bug JDK-4117141
    }

    @Override
    public void onGameChange(Game.Data data)
    {
        this.updateButtons(data);
    }

    @Override
    public void onGameEnd(Data data)
    {
    }

}
