package engines.nuwanisl;

import backend.Player;
import resources.BuiltinEngineResource;

public class NuwaniSLResource extends BuiltinEngineResource
{
	public NuwaniSLResource()
	{
		super(NuwaniSL.NAME, (byte[]) null);
	}

	@Override
	public Player createPlayer(int playerNumber) throws Exception
	{
		return new NuwaniSL(playerNumber);
	}
}
