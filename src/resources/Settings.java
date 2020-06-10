package resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import frontend.VolatileLogger;

public class Settings
{
	private static final File file;

	public static final Properties settings;

	static {
		file = Resources.workingdir.resolve("settings.properties").toFile();
		settings = new Properties();

		try (FileInputStream in = new FileInputStream(file.getAbsolutePath())) {
			settings.load(in);
		} catch (Exception e) {
			VolatileLogger.logf(e, "loading settings");
		}
	}

	public static void save()
	{
		try (FileOutputStream out = new FileOutputStream(file.getAbsolutePath())) {
			settings.store(out, "hi");
		} catch (Exception e) {
			VolatileLogger.logf(e, "saving settings");
		}
	}
}
