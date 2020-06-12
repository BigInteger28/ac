package frontend;

import java.awt.Window;

public interface FrontendController
{
	Window getWindow();
	void startNewGameAdv();
	void startNewGame();
	void undoMove();
}
