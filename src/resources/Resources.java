package resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.file.Paths;

import frontend.VolatileLogger;

public class Resources
{
	public static File workingdir;

	static {
		workingdir = Paths.get(".").toAbsolutePath().normalize().toFile();

		String loc = Resources.class.getResource(Resources.class.getSimpleName() + ".class").toString();
		if (loc.startsWith("jar:")) {
			String wdir = loc.substring(9); // remove jar:file:
			int lastSeparatorIdx = wdir.lastIndexOf("!/");
			final String path = wdir.substring(0, lastSeparatorIdx);
			File jarfile = new File(path);
			if (!jarfile.exists()) {
				try {
					jarfile = new File(URLDecoder.decode(path, "utf-8"));
				} catch (UnsupportedEncodingException e) {
					System.err.println("failed to decode path url");
				}
			}
			workingdir = jarfile.getParentFile();
		}
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
