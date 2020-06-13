package backend;

import engines.Database;

/**
 * Implementations must not hold any state!
 */
public interface Player extends Resource
{
	default void load() throws Exception
	{
	}

	/**
	 * @param p player's number
	 */
	int doMove(int p, Game.Data data);

	/**
	 * @param p player's number
	 */
	default void onGameEnd(int p, Game.Data gamedata)
	{
	}

	boolean canUseDatabase();

	default void useDatabase(Database db)
	{
	}
}
