package backend;

/**
 * Implementations must not hold any state!
 */
public abstract class Player implements Resource
{
	public void load() throws Exception
	{
	}

	/**
	 * @param p player's number
	 */
	public abstract int doMove(int p, Database db, Game.Data data);

	/**
	 * @param p player's number
	 */
	public void onGameEnd(int p, Database db, Game.Data gamedata)
	{
	}

	public abstract boolean canUseDatabase();
}
