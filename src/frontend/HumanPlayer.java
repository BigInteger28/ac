package frontend;

import backend.Game;
import backend.HumanControlled;
import backend.Player;
import resources.PlayerResource;

public class HumanPlayer implements Player, HumanControlled
{
	public static final PlayerResource RESOURCEINSTANCE = new HumanPlayerResource();
	public static final HumanPlayer INSTANCE = new HumanPlayer();

	static final int chosenElement[] = { -1, -1 };

	@Override
	public String getName()
	{
		return "Human";
	}

	@Override
	public int doMove(int p, Game.Data data)
	{
		int element = chosenElement[p];
		chosenElement[p] = -1;
		return element;
	}

	private static class HumanPlayerResource extends PlayerResource
	{
		@Override
		public String getName()
		{
			return "<Human player>";
		}

		@Override
		public String getPath()
		{
			return null;
		}

		@Override
		public int getType()
		{
			return TYPE_HUMAN;
		}

		@Override
		public Player createPlayer()
		{
			return HumanPlayer.INSTANCE;
		}
	}
}
