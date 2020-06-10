package resources;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import frontend.VolatileLogger;

public class Resources
{
	public static Path workingdir;

	static {
		workingdir = Paths.get(".").toAbsolutePath().normalize();
	}

	public static File ptof(String path)
	{
		if (path == null) {
			return null;
		}
		try {
			return Paths.get(path).toAbsolutePath().normalize().toFile();
		} catch (Exception e) {
			VolatileLogger.logf(e, "path '%s' to file", path);
		}
		return null;
	}

	public static byte[] readFileCharacters(File file, int len) throws Exception
	{
		final byte[] data = new byte[len];
		try (FileInputStream in = new FileInputStream(file)) {
			data[0] = (byte) in.read();
			len--;
			if ((data[0] & 0xEF) == 0xEF) {
				// BOM
				in.read();
				in.read();
				data[0] = (byte) in.read();
			}
			if (in.available() < len) {
				throw new Exception("unexpected EOF");
			}
			final int read = in.read(data, /*off*/ 1, len);
			if (read != len) {
				throw new Exception("unexpected EOF");
			}
		}
		return data;
	}
}
