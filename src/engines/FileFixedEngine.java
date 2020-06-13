package engines;

import java.io.File;

import resources.Resources;

import static common.Constants.*;

public class FileFixedEngine extends FixedEngine
{
	private final File file;

	public FileFixedEngine(File file)
	{
		super(file.getName());
		this.file = file;
	}

	@Override
	public String getPath()
	{
		return this.file.getParentFile().getAbsolutePath();
	}

	@Override
	public void load() throws Exception
	{
		byte[] data;
		int read;

		data = new byte[9];
		read = Resources.readFileCharacters(this.file, data, 8);
		if (read < 8) {
			new Exception("need at least 8 elements, got " + read);
		}

		this.moves = new int[9];
		byte[] elementsneeded = { 2, 2, 2, 2, 1 };
		byte[] elementsused = new byte[5];
		for (int i = 0; i < 8; i++) {
			int move = data[i] | 0x20;
			switch (move) {
			case 'w': move = WATER; break;
			case 'v': move = FIRE; break;
			case 'a': move = EARTH; break;
			case 'l': move = AIR; break;
			case 'd': move = DEFENSE; break;
			default: throw new Exception("invalid element: '" + (char) move + "'");
			}
			this.moves[i] = move;
			elementsused[move]++;
		}

		for (byte i = 0; i < 5; i++) {
			if (elementsused[i] < elementsneeded[i]) {
				data[8] = i;
				elementsused[i]++;
				this.moves[8] = i;
				break;
			}
		}

		for (byte i = 0; i < 5; i++) {
			if (elementsused[i] != elementsneeded[i]) {
				String msg = String.format("need %d of %c, got only %d", elementsneeded[i], CHARELEMENTS[i], elementsused[i]);
				throw new Exception(msg);
			}
		}
	}
}
