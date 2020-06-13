package resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

import frontend.VolatileLogger;

public class Settings
{
	public static final Properties settings;

	public static final String SETTING_LAST_SAVED_GAME_PATH = "saved.games.lastpath";

	private static final File file;

	static {
		file = new File(Resources.workingdir, "settings.properties");
		settings = new Properties();

		if (file.exists()) {
			try (FileInputStream in = new FileInputStream(file.getAbsolutePath())) {
				settings.load(in);
			} catch (Exception e) {
				VolatileLogger.logf(e, "loading settings");
			}
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
