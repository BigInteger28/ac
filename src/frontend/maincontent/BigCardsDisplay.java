package frontend.maincontent;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import backend.Game;
import frontend.Main;

public class BigCardsDisplay extends JPanel
{
	private static final int PADDING = 16;

	private Game game;

	public BigCardsDisplay(Game game)
	{
		this.game = game;

		Dimension dim = new Dimension(new Dimension(9 * (70 + 2) + 8 * PADDING, 2 * (88 + 2) + PADDING));
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

		minx = (size.width - minSize.width) / 2;
		miny = (size.height - minSize.height) / 2;

		move = this.game.data == null ? 0 : this.game.data.getCurrentMove();
		for (int i = 0; i < 9; i++) {
			x = minx + 72 * i + PADDING * i;
			y = miny;
			g.drawRect(x, y, 71, 89);
			if (i < move) {
				g.drawImage(Main.bigElementImages[this.game.data.getMove(0, i)], x + 1, y + 1, null);
			} else if (i == 0) {
				g.drawImage(Main.backImage, x + 1, y + 1, null);
			}
			y = miny + 88 + 2 + PADDING;
			g.drawRect(x, y, 71, 89);
			if (i < move) {
				g.drawImage(Main.bigElementImages[this.game.data.getMove(1, i)], x + 1, y + 1, null);
			} else if (i == 0) {
				g.drawImage(Main.backImage, x + 1, y + 1, null);
			}
		}
	}
}
