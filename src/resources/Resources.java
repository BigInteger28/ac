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

	public static int readFileCharacters(File file, byte[] into, int maxLen) throws Exception
	{
		int i;
		int read;

		try (FileInputStream in = new FileInputStream(file)) {
			i = in.read();
			if (i == -1) {
				return 0;
			}
			if ((i & 0xEF) == 0xEF) {
				// BOM
				if (in.read() == -1) {
					return 0;
				}
				if (in.read() == -1) {
					return 0;
				}
				i = in.read();
				if (i == -1) {
					return 0;
				}
			}
			into[0] = (byte) i;
			read = 1;
			maxLen--;
			for (;;) {
				i = in.read(into, read, maxLen);
				if (i == -1) {
					return read;
				}
				read += i;
				if (maxLen == 0) {
					return read;
				}
				maxLen -= i;
			}
		}
	}
}
