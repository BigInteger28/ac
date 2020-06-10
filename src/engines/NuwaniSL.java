package engines;

import backend.Game.Data;
import backend.Player;
import resources.PlayerResource;

public class NuwaniSL implements Player
{
	private static String NAME = "Nuwani SL";

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public int doMove(int p, Data data)
	{
		return 0;
	}

	@Override
	public void onGameStart(Data gamedata, int yourPlayerNumber)
	{
	}

	@Override
	public void onMoveDone(int yourMove, int otherMove, int score)
	{
	}

	@Override
	public void onGameEnd(Data gamedata)
	{
	}

	@Override
	public boolean isHumanControlled()
	{
		return false;
	}

	@Override
	public boolean canUseDatabase()
	{
		return false;
	}

	@Override
	public void useDatabase(Database db)
	{
	}

	public static class Resource extends PlayerResource
	{
		@Override
		public Player createPlayer(int playerNumber) throws Exception
		{
			return new Synergy2PROEngine();
		}

		@Override
		public String getName()
		{
			return '<' + NAME + '>';
		}

		@Override
		public String getPath()
		{
			return null;
		}

		@Override
		public int getType()
		{
			return PlayerResource.TYPE_BUILTIN;
		}
	}
}
