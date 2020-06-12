package frontend.components;

import java.awt.Graphics;

import javax.swing.JButton;

public class HideableButton extends JButton
{
	public boolean draw = true;

	public HideableButton(String text)
	{
		super(text);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		if (this.draw) { 
			super.paintComponent(g);
		}
	}
}
