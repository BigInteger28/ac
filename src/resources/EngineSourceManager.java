package resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import engines.NuwaniSL;
import engines.Synergy2PROEngine;
import frontend.VolatileLogger;

public class EngineSourceManager
{
	private static final String LOCATIONSFILENAME;
	private static final File locationsfile;
	private static final List<File> locations;
	private static final List<PlayerResource> BUILTINENGINES;

	private static int lastAmountOfResources;

	static {
		LOCATIONSFILENAME = "locations.txt";
		lastAmountOfResources = 100;
		locationsfile = Resources.workingdir.resolve(LOCATIONSFILENAME).toFile();
		locations = new ArrayList<>();
		readLocations();
		BUILTINENGINES = new ArrayList<>();
		BUILTINENGINES.add(bi("sYnergY 1", 6, 6, 6, 6, 6, 6, 6, 6));
		BUILTINENGINES.add(new Synergy2PROEngine.Resource());
		BUILTINENGINES.add(bi("sYnergY 3", 9, 6, 9, 8, 7, 6, 7, 8));
		BUILTINENGINES.add(bi("Junior", 9, 8, 7, 6, 9, 6, 7, 8));
		BUILTINENGINES.add(bi("GreenFrog", 8, 8, 8, 8, 6, 8, 9, 9));
		BUILTINENGINES.add(bi("Jaguar", 8, 8, 7, 7, 6, 6, 8, 7));
		BUILTINENGINES.add(bi("Red", 7, 6, 9, 7, 6, 8, 8, 7));
		BUILTINENGINES.add(bi("Deep Red", 9, 9, 8, 7, 7, 8, 6, 7));
		BUILTINENGINES.add(bi("Derp", 8, 6, 8, 7, 7, 6, 8, 8));
		BUILTINENGINES.add(bi("Schildpad", 9, 9, 9, 9, 9, 9, 9, 9));
		BUILTINENGINES.add(bi("Botje 2 PRO", 9, 7, 8, 7, 8, 7, 9, 7));
		BUILTINENGINES.add(new NuwaniSL.Resource());
	}

	private static BuiltinEngineResource bi(String name, int... depths)
	{
		final byte[] d = new byte[9];
		for (int i = 0; i < depths.length; i++) {
			d[i] = (byte) depths[i];
		}
		return new BuiltinEngineResource(name, d);
	}

	public static void collectResources(ArrayList<PlayerResource> playerList, ArrayList<DatabaseResource> dbList,
		boolean includeHuman)
	{
		playerList.ensureCapacity(lastAmountOfResources);

		final Stack<File> walkerstack = new Stack<>();
		for (File loc : locations) {
			walkerstack.push(loc);
			while (!walkerstack.isEmpty()) {
				final File dir = walkerstack.pop();
				if (!dir.isDirectory()) {
					continue;
				}

				for (File child : dir.listFiles()) {
					if (child.isDirectory()) {
						walkerstack.push(child);
						continue;
					}

					if (!child.isFile()) {
						continue;
					}

					final String name = child.getName();
					final int dotloc = name.lastIndexOf('.');
					if (dotloc == -1) {
						continue;
					}
					final String extension = name.substring(dotloc);

					if (".ak".equals(extension)) {
						addResource(playerList, new FixedEngineResource(child));
					} else if (".akb".equals(extension)) {
						addResource(playerList, new DepthEngineResource(child));
					} else if (".adb".equals(extension)) {
						addResource(dbList, new DatabaseResource(child));
					}
				}
			}
		}

		int bpos = 0;
		if (includeHuman) {
			playerList.add(bpos++, new HumanPlayerResource());
		}
		playerList.addAll(bpos, BUILTINENGINES);

		lastAmountOfResources = playerList.size() + 20;
	}

	private static <T extends Resource> void addResource(List<T> list, T resource)
	{
		if (list.isEmpty()) {
			list.add(resource);
			return;
		}

		final String name = resource.getName().toLowerCase();

		int min = 0;
		int max = list.size();
		int pos = max / 2;

		for (;;) {
			final String othername = list.get(pos).getName().toLowerCase();
			if (name.compareTo(othername) >= 0) {
				min = pos + 1;
			} else {
				max = pos;
			}
			if (max - min < 1) {
				pos = max;
				break;
			}
			pos = (max + min) / 2;
			if (pos >= list.size()) {
				break;
			}
			continue;
		}

		list.add(pos, resource);
	}

	private static void readLocations()
	{
		locations.clear();
		if (!locationsfile.exists()) {
			return;
		}

		try {
			final List<String> lines = Files.readAllLines(locationsfile.toPath());
			for (String line : lines) {
				final File file = Resources.ptof(line);
				if (file != null) {
					locations.add(file);
				}
			}
		} catch (IOException e) {
			VolatileLogger.logf(e, "reading %s file", LOCATIONSFILENAME);
		}
	}

	public static List<File> getLocations()
	{
		return new ArrayList<>(locations);
	}

	public static void setLocations(List<File> locations)
	{
		EngineSourceManager.locations.clear();
		EngineSourceManager.locations.addAll(locations);

		final List<String> lines = new ArrayList<>();
		for (File file : locations) {
			lines.add(file.getAbsolutePath());
		}

		try {
			Files.write(locationsfile.toPath(), lines, StandardOpenOption.CREATE,
				StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException e) {
			VolatileLogger.logf(e, "writing %s file", LOCATIONSFILENAME);
		}
	}

}
