package frontend.util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

public class SimpleKeyListener implements KeyListener
{
	private final Consumer<KeyEvent> listener;

	public SimpleKeyListener(Consumer<KeyEvent> listener)
	{
		this.listener = listener;
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
		this.listener.accept(e);
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
	}
}
