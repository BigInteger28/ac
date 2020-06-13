package engines;

import backend.Database;
import backend.Game;
import backend.Player;
import backend.ResourceType;

public class FixedEngine extends Player
{
	private final String name;

	protected int moves[];

	public FixedEngine(String name)
	{
		this.name = name;
	}

	public FixedEngine(String name, int[] moves)
	{
		this.name = name;
		this.moves = moves;
	}

	@Override
	public ResourceType getType()
	{
		return ResourceType.FIXED;
	}

	@Override
	public String getPath()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public int doMove(int p, Database db, Game.Data data)
	{
		if (db != null) {
			int dbres = db.findEntry(data, p);
			if (dbres != -1 && data.getElementsLeft(p, dbres) > 0) {
				return dbres;
			}
		}
		return this.moves[data.getCurrentMove()];
	}

	@Override
	public boolean canUseDatabase()
	{
		return true;
	}
}
