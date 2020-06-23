package frontend.components;

import java.awt.Graphics;

import javax.swing.JButton;

public class HideableButton extends JButton
{
	public HideableButton(String text)
	{
		super(text);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		if (this.isBorderPainted()) {
			super.paintComponent(g);
		} else {
			g.setColor(this.getParent().getBackground());
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}
	}
}
