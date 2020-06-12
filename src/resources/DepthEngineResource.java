package resources;

import java.io.File;

import backend.Player;
import engines.DepthEngine;

public class DepthEngineResource extends PlayerResource
{
	private final File resource;

	public DepthEngineResource(File resource)
	{
		this.resource = resource;
	}

	@Override
	public String getName()
	{
		return this.resource.getName();
	}

	@Override
	public String getPath()
	{
		return this.resource.getParentFile().getAbsolutePath();
	}

	@Override
	public int getType()
	{
		return TYPE_DEPTH;
	}

	@Override
	public Player createPlayer() throws Exception
	{
		byte[] data;
		int size;

		data = new byte[9];
		size = Resources.readFileCharacters(this.resource, data, 9);

		if (size < 8) {
			throw new Exception("need at least 8 depths, got " + size);
		}

		if (size < 9) {
			data[8] = '0';
		}

		for (int i = 0; i < 9; i++) {
			if (data[i] < '0' || '9' < data[i]) {
				throw new Exception("invalid depth: " + (char) (data[i] & 0xFF));
			}
			data[i] -= '0';
		}

		return new DepthEngine(this.resource.getName(), data);
	}
}
