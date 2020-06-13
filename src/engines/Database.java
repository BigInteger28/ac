package engines;

import java.util.List;

import backend.Game;
import backend.Resource;
import backend.ResourceType;

public class Database implements Resource
{
	private final String name;

	/**
	 * db entries:
	 * lowest 4 bits: result element
	 * next 4 bits: element 1
	 * next 4 bits: element 2
	 * ...
	 * unused nibbles: 0x77
	 */
	protected List<Integer> db;

	public Database(String name)
	{
		this.name = name;
	}

	public Database(String name, List<Integer> db)
	{
		this.name = name;
		this.db = db;
	}

	public void load() throws Exception
	{
	}

	@Override
	public ResourceType getType()
	{
		return ResourceType.DATABASE;
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public String getPath()
	{
		return null;
	}

	public int findEntry(Game.Data data, int playerNumber)
	{
		final int currentMove = data.getCurrentMove();
		if (currentMove == 0 || 7 < currentMove) {
			return -1;
		}

		final int p = playerNumber ^ 1;
		int hash = 0x77777777;
		for (int i = 0; i < currentMove; i++) {
			hash = hash << 4 | (data.getMove(p, i) & 0xF);
		}
		hash <<= 4;

		for (int v : db) {
			if ((v & 0xFFFFFFF0) == hash) {
				return v & 0xF;
			}
		}

		return -1;
	}
}
