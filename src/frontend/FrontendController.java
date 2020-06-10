package frontend;

import java.awt.Window;

public interface FrontendController
{
	Window getWindow();

	void startNewGameAdv();

	void startNewGame();

	void addGameChangeListener(GameChangeListener listener);

	void chooseElement(int player, int element);
}
