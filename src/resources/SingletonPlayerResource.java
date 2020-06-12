package resources;

import backend.Player;

public class SingletonPlayerResource extends PlayerResource
{
	private Player player;

	public SingletonPlayerResource(Player player)
	{
		this.player = player;
	}

	@Override
	public Player createPlayer() throws Exception
	{
		return this.player;
	}

	@Override
	public String getName()
	{
		return this.player.getName();
	}

	@Override
	public String getPath()
	{
		return null;
	}

	@Override
	public int getType()
	{
		return TYPE_BUILTIN;
	}
}
