package frontend.components;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JTextField;

public class JTextFieldWithPlaceholder extends JTextField
{
	private String placeholder;

	public JTextFieldWithPlaceholder(String placeholder)
	{
		this.placeholder = placeholder;
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		int lineHeight;

		super.paintComponent(g);
		if (this.getText().isEmpty()) {
			g.setColor(new Color(0x999999));
			g.setFont(this.getFont());
			lineHeight = g.getFontMetrics().getHeight();
			// font draw y position is from bottom of the text
			g.drawString(this.placeholder, 6, this.getHeight() / 2 + (int) (lineHeight * .35f));
		}
	}
}
