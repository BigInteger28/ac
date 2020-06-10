package frontend.util;

import java.awt.*;
import java.awt.event.WindowEvent;

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
}
