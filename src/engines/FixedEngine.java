package engines;

import backend.Game;
import backend.Player;
import backend.ResourceType;

public class FixedEngine implements Player
{
	private final String name;

	private Database db;

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
		if (this.db != null) {
			return this.name + " + " + this.db.getName();
		}
		return this.name;
	}

	@Override
	public int doMove(int p, Game.Data data)
	{
		if (this.db != null) {
			int dbres = this.db.findEntry(data, p);
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

	@Override
	public void useDatabase(Database db)
	{
		this.db = db;
	}
}
