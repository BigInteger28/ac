package frontend;

import backend.Game;
import backend.Player;
import engines.Database;
import resources.PlayerResource;

public class HumanPlayer implements Player
{
	public static final PlayerResource RESOURCEINSTANCE = new HumanPlayerResource();

	private int chosenElement;

	void setChosenElement(int element)
	{
		this.chosenElement = element;
	}

	@Override
	public String getName()
	{
		return "Human";
	}

	@Override
	public int doMove(int p, Game.Data data)
	{
		return this.chosenElement;
	}

	@Override
	public void onGameStart(Game.Data data, int yourPlayerNumber)
	{
		this.chosenElement = -1;
	}

	@Override
	public void onMoveDone(int yourMove, int otherMove, int score)
	{
		this.chosenElement = -1;
	}

	@Override
	public void onGameEnd(Game.Data data)
	{
	}

	@Override
	public boolean isHumanControlled()
	{
		return true;
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
		public Player createPlayer(int playerNumber)
		{
			return Main.humanPlayers[playerNumber];
		}
	}
}
