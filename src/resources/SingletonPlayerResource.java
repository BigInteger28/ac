package resources;

import backend.Player;

public class SingletonPlayerResource extends PlayerResource
{
	private Player player;
	private String name;

	public SingletonPlayerResource(Player player)
	{
		this.player = player;
		this.name = player.getName();
	}

	public SingletonPlayerResource(Player player, String name)
	{
		this.player = player;
		this.name = name;
	}

	@Override
	public Player createPlayer() throws Exception
	{
		return this.player;
	}

	@Override
	public String getName()
	{
		return '<' + this.name + '>';
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
