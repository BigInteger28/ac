package frontend.components;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JList;
import javax.swing.ListModel;

public class HideableList<T> extends JList<T>
{
	private String hiddenmsg;

	public HideableList(ListModel<T> model)
	{
		super(model);
	}

	public void hideWithMessage(String msg)
	{
		this.hiddenmsg = msg;
		this.repaint();
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Dimension size;

		if (this.hiddenmsg == null) {
			super.paintComponent(g);
		} else {
			size = this.getSize();
			g.setColor(this.getParent().getBackground());
			g.fillRect(0, 0, size.width, size.height);
		}
	}
}
