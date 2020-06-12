package resources;

import backend.Player;
import engines.DepthEngine;

public class BuiltinEngineResource extends PlayerResource
{
	private final String name;
	private final byte[] depths;

	public BuiltinEngineResource(String name, byte[] depths)
	{
		this.name = name;
		this.depths = depths;
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

	@Override
	public Player createPlayer() throws Exception
	{
		return new DepthEngine(this.name, this.depths);
	}
}
