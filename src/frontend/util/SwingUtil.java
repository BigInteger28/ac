package frontend.util;

import java.awt.*;
import java.awt.event.WindowEvent;

import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

public class SwingUtil
{
	public static Font deriveFont(Font original, boolean bold, float relativeSize)
	{
		final float size = original.getSize() * relativeSize;
		int style = Font.PLAIN;
		if (bold) {
			style |= Font.BOLD;
		}
		return original.deriveFont(style, size);
	}

	public static void close(Window window)
	{
		window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	}

	public static TitledBorder titledBorder(String title)
	{
		TitledBorder border = new TitledBorder(title);
		border.setTitleColor(new Color(0x0000FF));
		return border;
	}

	public static Border tripleBorder(Border outer, Border mid, Border inner)
	{
		return new CompoundBorder(new CompoundBorder(outer, mid), inner);
	}

	public static JPanel wrap(Component c, Border...borders)
	{
		Border border;

		JPanel pnl = new JPanel(new BorderLayout());
		pnl.add(c, BorderLayout.CENTER);
		if (borders.length > 1) {
			border = new CompoundBorder(borders[0], borders[1]);
			for (int i = 2; i < borders.length; i++) {
				border = new CompoundBorder(border, borders[i]);
			}
			pnl.setBorder(border);
		} else if (borders.length > 0) {
			pnl.setBorder(borders[0]);
		}
		return pnl;
	}
}
