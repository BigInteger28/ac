package engines;

import backend.Game;
import backend.Player;
import backend.ResourceType;

import static common.Constants.*;

public class DepthEngine implements Player
{
	private final String name;

	private ResourceType type;
	private Database db;

	protected byte[] depths;

	public DepthEngine(String name)
	{
		this.name = name;
		this.type = ResourceType.DEPTH;
	}

	public DepthEngine(String name, int...depths)
	{
		this.name = name;
		this.type = ResourceType.BUILTIN;
		this.depths = new byte[9];
		for (int i = 0; i < depths.length; i++) {
			this.depths[i] = (byte) depths[i];
		}
	}

	@Override
	public ResourceType getType()
	{
		return this.type;
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
	public String getPath()
	{
		return null;
	}

	@Override
	public int doMove(int p, Game.Data data)
	{
		final int move = data.getCurrentMove();
		if (move == 0) {
			return DEFENSE;
		}

		// last move, put the element we have left
		// (if this is removed, the 'second opinion' feature would freeze if it's the
		// last move and the only element left is defense)
		if (move == 8) {
			for (int i = 0; i < 5; i++) {
				if (data.getElementsLeft(p, i) > 0) {
					return i;
				}
			}
		}

		if (this.db != null) {
			int dbres = db.findEntry(data, p);
			if (dbres != -1 && data.getElementsLeft(p, dbres) > 0) {
				return dbres;
			}
		}

		int element = data.getMove(p ^ 1, move - 1);
		if (element == DEFENSE) {
			element = AIR; // see engine.vb:53
		}

		final int[] onedepth = new int[5];
		onedepth[WATER] = AIR;
		onedepth[FIRE] = WATER;
		onedepth[EARTH] = FIRE;
		onedepth[AIR] = EARTH;

		int i = (this.depths[move] + 4) % 4;
		while (i-- > 0 || data.getElementsLeft(p, element) == 0) {
			element = onedepth[element];
		}

		return element;
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
