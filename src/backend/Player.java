package backend;

import engines.Database;

public interface Player
{
	String getName();

	int doMove(int p, Game.Data data);

	void onGameStart(Game.Data gamedata, int yourPlayerNumber);

	void onMoveDone(int yourMove, int otherMove, int score);

	void onGameEnd(Game.Data gamedata);

	boolean isHumanControlled();

	boolean canUseDatabase();

	void useDatabase(Database db);
}
