package engines.nuwanisl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import common.ErrorHandler;
import resources.EngineSourceManager;

class DB
{
	private static byte MARK_START_ENGINE_DATA = 8;
	private static byte MARK_START_PLAYER_DATA = 7;
	private static byte MARK_END_DATA = 6;

	private static String DB_FILE = "nuwani.sl";
	private static String DB_BACKUP_FILE = "nuwani.sl.backup";

	static class Variant
	{
		// int for depths:
		// 00 00 00 00
		// x7 65 43 21
		// set first bit for metadata
		// int for amount played
		// 4^7 = 16384
		int data[] = new int[16384 * 2];
		int numData;

		Variant copy()
		{
			Variant v = new Variant();
			System.arraycopy(this.data, 0, v.data, 0, this.data.length);
			v.numData = this.numData;
			return v;
		}

		int indexFor(int value, int mask)
		{
			if (mask == -1) {
				for (int i = 0; i < this.numData; i++) {
					if (this.data[i * 2] == value) {
						return i * 2;
					}
				}
			} else {
				for (int i = 0; i < this.numData; i++) {
					if ((this.data[i * 2] & mask) == value) {
						return i * 2;
					}
				}
			}
			return -1;
		}
	}

	static DB.Variant forEngines = new DB.Variant();
	static DB.Variant forPlayers = new DB.Variant();

	static void init()
	{
		reset();

		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			File databaseFile = findDatabaseFile();
			if (databaseFile != null) {
				writeDatabase(databaseFile);
			} else {
				List<File> locations = EngineSourceManager.getLocations();
				if (!locations.isEmpty()) {
					writeDatabase(new File(locations.get(0), "nuwani.sl"));
				}
			}
		}));

		File databaseFile = findDatabaseFile();
		if (databaseFile != null) {
			readDatabase(databaseFile);
		}
	}

	static void reset()
	{
		forEngines.numData = forPlayers.numData = 0;
	}

	private static File findDatabaseFile()
	{
		for (File dir : EngineSourceManager.getLocations()) {
			File f = new File(dir, DB_FILE);
			if (f.exists() && f.isFile()) {
				return f;
			}
		}
		return null;
	}

	private static void simpleBackupDatabase(File databaseFile)
	{
		File copy = new File(databaseFile.getParentFile(), DB_BACKUP_FILE);
		try {
			Files.copy(databaseFile.toPath(), copy.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			ErrorHandler.handler.handleException(e, "Failed to make a backup of the Nuwani SL database");
		}
	}

	private static void readDatabase(File databaseFile)
	{
		int data;

		reset();
		simpleBackupDatabase(databaseFile);
		try (FileInputStream in = new FileInputStream(databaseFile)) {
			for (;;) {
				data = in.read();
				if (data == -1) {
					return;
				} else if (data == MARK_START_ENGINE_DATA) {
					readDatabaseVariant(in, forEngines);
				} else if (data == MARK_START_PLAYER_DATA) {
					readDatabaseVariant(in, forPlayers);
				} else {
					System.out.printf("wrong data in SL db: %02X%n", data);
				}
			}
		} catch (IOException e) {
			ErrorHandler.handler.handleException(e);
		}
	}

	private static void readDatabaseVariant(InputStream in, DB.Variant variant) throws IOException
	{
		int data, count, a, b;

		count = a = b = 0;
		for (;;) {
			data = in.read();
			if (data == -1) {
				return;
			} else if (count == 0 && data == MARK_END_DATA) {
				break;
			}
			if (count < 4) {
				a |= ((data & 0xFF) << (count * 8));
			} else if (count < 8) {
				b |= ((data & 0xFF) << (count * 8));
			}
			count++;
			if (count == 8) {
				variant.data[variant.numData * 2] = a;
				variant.data[variant.numData * 2 + 1] = b;
				variant.numData++;
				count = 0;
				a = b = 0;
			}
		}
	}

	private static void writeDatabase(File databaseFile)
	{
		try (FileOutputStream out = new FileOutputStream(databaseFile)) {
			out.write(MARK_START_ENGINE_DATA);
			writeDatabaseVariant(out, forEngines);
			out.write(MARK_END_DATA);
			out.write(MARK_START_PLAYER_DATA);
			writeDatabaseVariant(out, forPlayers);
			out.write(MARK_END_DATA);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeDatabaseVariant(OutputStream out, DB.Variant variant) throws IOException
	{
		for (int i = 0; i < variant.numData; i++) {
			int a = variant.data[i * 2];
			int b = variant.data[i * 2 + 1];
			out.write((a) & 0xFF);
			out.write((a >>> 8) & 0xFF);
			out.write((a >>> 16) & 0xFF);
			out.write((a >>> 24) & 0xFF);
			out.write((b) & 0xFF);
			out.write((b >>> 8) & 0xFF);
			out.write((b >>> 16) & 0xFF);
			out.write((b >>> 24) & 0xFF);
		}
	}
}
