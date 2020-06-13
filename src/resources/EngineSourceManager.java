package resources;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import backend.Player;
import backend.Resource;
import engines.Database;
import engines.DepthEngine;
import engines.FileDatabase;
import engines.FileDepthEngine;
import engines.FileFixedEngine;
import engines.Synergy2PRO;
import engines.nuwanisl.NuwaniSL;
import frontend.VolatileLogger;

public class EngineSourceManager
{
	private static final String LOCATIONSFILENAME;
	private static final File locationsfile;
	private static final List<File> locations;
	private static final List<Player> BUILTINENGINES;

	private static int lastAmountOfResources;

	static {
		LOCATIONSFILENAME = "locations.txt";
		lastAmountOfResources = 100;
		locationsfile = new File(Resources.workingdir, LOCATIONSFILENAME);
		locations = new ArrayList<>();
		readLocations();
		BUILTINENGINES = new ArrayList<>();
		BUILTINENGINES.add(new DepthEngine("sYnergY 1", 6, 6, 6, 6, 6, 6, 6, 6));
		BUILTINENGINES.add(Synergy2PRO.INSTANCE);
		BUILTINENGINES.add(new DepthEngine("sYnergY 3", 9, 6, 9, 8, 7, 6, 7, 8));
		BUILTINENGINES.add(NuwaniSL.INSTANCE);
		BUILTINENGINES.add(new DepthEngine("Junior", 9, 8, 7, 6, 9, 6, 7, 8));
		BUILTINENGINES.add(new DepthEngine("GreenFrog", 8, 8, 8, 8, 6, 8, 9, 9));
		BUILTINENGINES.add(new DepthEngine("Jaguar", 8, 8, 7, 7, 6, 6, 8, 7));
		BUILTINENGINES.add(new DepthEngine("Red", 7, 6, 9, 7, 6, 8, 8, 7));
		BUILTINENGINES.add(new DepthEngine("Deep Red", 9, 9, 8, 7, 7, 8, 6, 7));
		BUILTINENGINES.add(new DepthEngine("Derp", 8, 6, 8, 7, 7, 6, 8, 8));
		BUILTINENGINES.add(new DepthEngine("Schildpad", 9, 9, 9, 9, 9, 9, 9, 9));
		BUILTINENGINES.add(new DepthEngine("Botje 2 PRO", 9, 7, 8, 7, 8, 7, 9, 7));
	}

	public static void collectResources(ArrayList<Player> playerList, ArrayList<Database> dbList, Player...extraResources)
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
						addResource(playerList, new FileFixedEngine(child));
					} else if (".akb".equals(extension)) {
						addResource(playerList, new FileDepthEngine(child));
					} else if (".adb".equals(extension)) {
						addResource(dbList, new FileDatabase(child));
					}
				}
			}
		}

		int bpos = 0;
		for (Player extraResource : extraResources) {
			playerList.add(bpos++, extraResource);
		}
		playerList.addAll(bpos, BUILTINENGINES);

		lastAmountOfResources = playerList.size() + 20;
	}

	public static Player makePlayerTryFindDatabase(Player player, ArrayList<Database> databases) throws Exception
	{
		player.load();
		if (player.canUseDatabase()) {
			for (Database database : databases) {
				if (database.getName().equals(player.getName())) {
					database.load();
					player.useDatabase(database);
				}
			}
		}
		return player;
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
