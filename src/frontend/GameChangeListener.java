package frontend;

import backend.Game;

public interface GameChangeListener
{
	void onGameStart(Game.Data data);

	void onGameChange(Game.Data data);

	void onGameEnd(Game.Data data);
}
