package frontend;

import backend.Game;
import backend.HumanControlled;
import backend.Player;
import backend.ResourceType;

public class HumanPlayer implements Player, HumanControlled
{
	public static final HumanPlayer INSTANCE = new HumanPlayer();

	static final int chosenElement[] = { -1, -1 };

	@Override
	public String getName()
	{
		return "Human";
	}

	@Override
	public ResourceType getType()
	{
		return ResourceType.HUMAN;
	}

	@Override
	public String getPath()
	{
		return null;
	}

	@Override
	public boolean canUseDatabase()
	{
		return false;
	}

	@Override
	public int doMove(int p, Game.Data data)
	{
		int element = chosenElement[p];
		chosenElement[p] = -1;
		return element;
	}
}
