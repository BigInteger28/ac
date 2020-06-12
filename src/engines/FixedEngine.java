package engines;

import backend.Game;
import backend.Player;

public class FixedEngine implements Player
{
	private final String name;
	private final int moves[];

	private Database db;

	public FixedEngine(String name, int[] moves)
	{
		this.name = name;
		this.moves = moves;
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
