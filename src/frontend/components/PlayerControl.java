package frontend.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.function.BiConsumer;

import static common.Constants.STANDARDELEMENTS;

public class PlayerControl extends JPanel {

    private final TitledBorder titleBorder;
    private static final int[] BUTTONCOLORS = { 0xFFFF00, 0x008000, 0xFF0000, 0x0000FF, 0x808080 };

    public PlayerControl(String name, int playerNumber, BiConsumer<Integer, Integer> elementChooseListener) {
        this.setLayout(new GridLayout(0, 5, 5, 0));
        this.titleBorder = new DefaultTitledBorder(name);
        final Border innerBorder = new EmptyBorder(2, 4, 4, 4);
        this.setBorder(new CompoundBorder(titleBorder, innerBorder));

        for (int i = 0; i < 5; i++) {
            final JButton button = new JButton(STANDARDELEMENTS[i]);
            button.setBackground(new Color(BUTTONCOLORS[i]));
            button.setFocusable(false);
            this.add(button);
            final int element = i;
            button.addActionListener(e -> elementChooseListener.accept(playerNumber, element));
        }
    }

    public void setPlayerName(String name) {
        this.titleBorder.setTitle(name);
    }

}
