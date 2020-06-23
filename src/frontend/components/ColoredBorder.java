package frontend.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.border.EmptyBorder;

public class ColoredBorder extends EmptyBorder
{
	public Color color;

	public ColoredBorder(int top, int left, int bottom, int right)
	{
		super(top, left, bottom, right);
	}

	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height)
	{
		if (color != null) {
			g.setColor(this.color);
			g.fillRect(x, y, width, height);
		}
	}
}
