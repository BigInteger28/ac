package backend;

import engines.Database;

/**
 * Implementations must not hold any state!
 */
public interface Player
{
	String getName();

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

	default boolean canUseDatabase()
	{
		return false;
	}

	default void useDatabase(Database db)
	{
	}
}
