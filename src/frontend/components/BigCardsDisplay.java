package frontend.components;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import backend.Game;
import frontend.Main;
import frontend.util.SwingUtil;

public class BigCardsDisplay extends JPanel
{
	private static final int PADDING = 16;

	private Game game;
	private Dimension innerSize;
	private Insets borderInsets;
	private boolean redrawRequested;

	public BigCardsDisplay(Game game)
	{
		Border border;
		Dimension dim;

		this.game = game;

		border = new CompoundBorder(new EmptyBorder(0, 10, 10, 10), SwingUtil.titledBorder("Cards"));
		this.borderInsets = border.getBorderInsets(this);
		this.setBorder(border);

		this.innerSize = new Dimension(9 * (70 + 2) + 8 * PADDING, 2 * (88 + 2) + PADDING);
		
		dim = new Dimension(innerSize);
		dim.width += borderInsets.left + borderInsets.right;
		dim.height += borderInsets.top + borderInsets.bottom;
		this.setMinimumSize(dim);
		this.setPreferredSize(dim);
	}

	@Override
	protected void paintComponent(Graphics g)
	{
		Dimension size, minSize;
		int minx, miny, x, y;
		int move;
		
		size = this.getSize();
		minSize = this.getMinimumSize();

		g.clearRect(0, 0, size.width, size.height);

		minx = (size.width - minSize.width) / 2 + this.borderInsets.left;
		miny = (size.height - minSize.height) / 2 + this.borderInsets.top;

		move = game.data == null ? 0 : game.data.getCurrentMove();
		for (int i = 0; i < 9; i++) {
			x = minx + 72 * i + PADDING * i;
			y = miny;
			g.drawRect(x, y, 71, 89);
			if (i < move) {
				g.drawImage(Main.bigElementImages[game.data.getMove(0, i)], x + 1, y + 1, null);
			} else if (i == 0) {
				g.drawImage(Main.backImage, x + 1, y + 1, null);
			}
			y = miny + 88 + 2 + PADDING;
			g.drawRect(x, y, 71, 89);
			if (i < move) {
				g.drawImage(Main.bigElementImages[game.data.getMove(1, i)], x + 1, y + 1, null);
			} else if (i == 0) {
				g.drawImage(Main.backImage, x + 1, y + 1, null);
			}
		}

		this.redrawRequested = false;
	}

	public void gameStateChanged()
	{
		if (!this.redrawRequested) {
			this.redrawRequested = true;
			SwingUtilities.invokeLater(() -> this.repaint());
		}
	}
}
