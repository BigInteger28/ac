package frontend.util;

import java.awt.*;
import java.awt.event.WindowEvent;

import javax.swing.border.TitledBorder;

import frontend.Main;

public class SwingUtil
{
	public static void close(Window window)
	{
		window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
	}

	public static TitledBorder titledBorder(String title)
	{
		TitledBorder border = new TitledBorder(title);
		border.setTitleColor(Main.TITLECOLOR);
		return border;
	}
}
