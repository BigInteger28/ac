package engines.nuwanisl;

import backend.Player;
import resources.PlayerResource;

public class NuwaniSLResource extends PlayerResource
{
	@Override
	public Player createPlayer(int playerNumber) throws Exception
	{
		return new NuwaniSL(playerNumber);
	}

	@Override
	public String getName()
	{
		return '<' + NuwaniSL.NAME + '>';
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
