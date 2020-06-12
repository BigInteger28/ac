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
	public Player createPlayer() throws Exception
	{
		return new NuwaniSL();
	}
}
