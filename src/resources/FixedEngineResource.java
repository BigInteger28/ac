package resources;

import java.io.File;

import backend.Player;
import engines.FixedEngine;

import static common.Constants.*;

public class FixedEngineResource extends PlayerResource
{
	private final File resource;

	public FixedEngineResource(File resource)
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
		return TYPE_FIXED;
	}

	@Override
	public Player createPlayer() throws Exception
	{
		byte[] data;
		int read;

		data = new byte[9];
		read = Resources.readFileCharacters(this.resource, data, 8);
		if (read < 8) {
			new Exception("need at least 8 elements, got " + read);
		}

		byte[] elementsneeded = { 2, 2, 2, 2, 1 };
		byte[] elementsused = new byte[5];
		int[] moves = new int[9];
		for (int i = 0; i < 8; i++) {
			int move = data[i] | 0x20;
			switch (move) {
			case 'w':
				move = WATER;
				break;
			case 'v':
				move = FIRE;
				break;
			case 'a':
				move = EARTH;
				break;
			case 'l':
				move = AIR;
				break;
			case 'd':
				move = DEFENSE;
				break;
			default:
				throw new Exception("invalid element: '" + (char) move + "'");
			}
			moves[i] = move;
			elementsused[move]++;
		}

		for (byte i = 0; i < 5; i++) {
			if (elementsused[i] < elementsneeded[i]) {
				data[8] = i;
				elementsused[i]++;
				break;
			}
		}

		for (byte i = 0; i < 5; i++) {
			if (elementsused[i] != elementsneeded[i]) {
				String msg = String.format("need %d of %c, got only %d", elementsneeded[i], CHARELEMENTS[i], elementsused[i]);
				throw new Exception(msg);
			}
		}

		return new FixedEngine(this.resource.getName(), moves);
	}
}
