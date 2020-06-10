package frontend.util;

import java.awt.*;
import java.awt.event.WindowEvent;

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

}
