package frontend.maincontent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import backend.Game;
import frontend.FrontendController;

import java.awt.*;

import static common.Constants.STANDARDELEMENTS;

public class PlayerControls extends JPanel
{
	private static final int[] BUTTONCOLORS = { 0x5050FF, 0xFF5050, 0x508050, 0xFFFF50, 0x808080 };

	private final JButton[] buttons;

	public PlayerControls(FrontendController controller, int player)
	{
		this.setLayout(new GridLayout(0, 5, 5, 0));
		this.buttons = new JButton[5];

		for (int i = 0; i < 5; i++) {
			final JButton button = new JButton(STANDARDELEMENTS[i] + " (?)");
			button.setFocusable(false);
			final int element = i;
			button.addActionListener(e -> {
				controller.chooseElement(player, element);
			});
			this.buttons[i] = button;

			final JPanel btnowner = new JPanel(new BorderLayout());
			btnowner.setBorder(new EmptyBorder(3, 3, 3, 3));
			btnowner.setBackground(new Color(BUTTONCOLORS[i]));
			btnowner.add(button);
			this.add(btnowner);
		}
	}

	public void updateButtons(Game.Data gamedata, int playerNumber)
	{
		boolean canPlayerPlay = !gamedata.isPlayerReady(playerNumber);
		for (int i = 0; i < 5; i++) {
			int elementsLeft = gamedata.getElementsLeft(playerNumber, i);
			this.buttons[i].setText(STANDARDELEMENTS[i] + " (" + elementsLeft + ")");
			this.buttons[i].setEnabled(canPlayerPlay && elementsLeft > 0 && gamedata.isHumanControlled(playerNumber));
		}
	}
}
