package engines;

import java.io.File;

import resources.Resources;

public class FileDepthEngine extends DepthEngine
{
	private final File file;

	public FileDepthEngine(File file)
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
		int size;

		this.depths = new byte[9];
		size = Resources.readFileCharacters(this.file, this.depths, 9);

		if (size < 8) {
			throw new Exception("need at least 8 depths, got " + size);
		}

		if (size < 9) {
			this.depths[8] = '0';
		}

		for (int i = 0; i < 9; i++) {
			if (this.depths[i] < '0' || '9' < this.depths[i]) {
				throw new Exception("invalid depth: " + (char) (this.depths[i] & 0xFF));
			}
			this.depths[i] -= '0';
		}
	}
}
