package engines.nuwanisl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import backend.ACMain;
import backend.Game;
import resources.DatabaseResource;
import resources.EngineSourceManager;
import resources.PlayerResource;

public class NuwaniSLTraining
{
	static int wins, losses, ties;

	public static void main(String[] args) throws Exception
	{
		DB.Variant originalDbEngines;
		int[][] myScore;
		int[][] theirScore;
		int GENS = 35;

		ACMain.main();

		originalDbEngines = DB.forEngines;
		DB.forEngines = new DB.Variant();

		ArrayList<PlayerResource> playerList = new ArrayList<>();
		ArrayList<DatabaseResource> dbList = new ArrayList<>();
		EngineSourceManager.collectResources(playerList, dbList);
		myScore = new int[playerList.size()][GENS];
		theirScore = new int[playerList.size()][GENS];
		int engines = 0;
		PlayerResource nuwaniResource = new NuwaniSLResource();
		Game g = new Game();
		for (int i = 0; i < GENS; i++) {
			DB.Variant cleanGenerationEngine = DB.forEngines.copy();
			DB.Variant buildingGenerationEngine = DB.forEngines.copy();
			wins = losses = ties = 0;
			int idx = 0;
			for (Iterator<PlayerResource> r = playerList.iterator() ; r.hasNext();) {
				PlayerResource playerResource = r.next();
				try {
					DB.forEngines = cleanGenerationEngine.copy();
					g.p1 = nuwaniResource.createPlayer(0);
					g.p2 = EngineSourceManager.makePlayerTryFindDatabase(playerResource, dbList, 1);
					g.startNewGame();

					int m, t;

					myScore[idx][i] = m = g.data.getScore(0);
					theirScore[idx][i] = t = g.data.getScore(1);
					idx++;

					if (m > t) {
						wins++;
					} else if (m == t) {
						ties++;
					} else {
						losses++;
					}

					for (int j = 0; j < cleanGenerationEngine.numData; j++) {
						DB.forEngines.data[j * 2 + 1] -= cleanGenerationEngine.data[j * 2 + 1];
					}
					nextNew: for (int j = 0; j < DB.forEngines.numData; j++) {
						for (int k = 0; k < buildingGenerationEngine.numData; k++) {
							if (DB.forEngines.data[j * 2] == buildingGenerationEngine.data[k * 2]) {
								buildingGenerationEngine.data[k * 2 + 1] += DB.forEngines.data[j * 2 + 1];
								continue nextNew;
							}
						}
						buildingGenerationEngine.data[buildingGenerationEngine.numData * 2] = DB.forEngines.data[j * 2];
						buildingGenerationEngine.data[buildingGenerationEngine.numData * 2 + 1] = DB.forEngines.data[j * 2 + 1];
						buildingGenerationEngine.numData++;
					}
				} catch (Exception e1) {
					r.remove();
					System.err.println(playerResource.getPath() + '/' + playerResource.getName());
					e1.printStackTrace();
				}
			}
			engines = idx;
			System.out.printf("win %d lose %d tie %d = %.02f <-> %.02f%n", wins, losses, ties,
				(wins + ties / 2.0f) / (wins + losses + ties),
				wins + ties / 2.0f - (wins + losses + ties) / 2.0f);
			DB.forEngines = buildingGenerationEngine;
		}

		DB.forEngines = originalDbEngines;
		ArrayList<String> lines = new ArrayList<>();
		ArrayList<String> lines2 = new ArrayList<>();
		lines.add("<table style='text-align:center' border='1'>");
		lines.add("<tr>");
		lines.add("<th></th>");
		for (int i = 0; i < GENS; i++) {
			lines.add("<th style='background:#99f'>Gen " + i + "</th>");
		}
		lines.add("</tr>");
		lines2.addAll(lines);
		for (int i = 0; i < engines; i++) {
			String name = playerList.get(i).getName().replace("<", "&lt;");
			lines.add("<tr><td style='background:#99f'>" + name + "</td>");
			lines2.add("<tr><td style='background:#99f'>" + name + "</td>");
			int last = 0;
			for (int j = 0; j < GENS; j++) {
				int m = myScore[i][j];
				int t = theirScore[i][j];
				if (m > t) {
					lines.add("<td style='background:#9f9'>");
				} else if (m < t) {
					lines.add("<td style='background:#f99'>");
				} else {
					lines.add("<td style='background:#ccc'>");
				}
				lines.add(m + " - " + t + "</td>");
				int now = m - t;
				if (j == 0 || now == last) {
					lines2.add("<td style='background:#ccc'>");
				} else if (now > last) {
					lines2.add("<td style='background:#9f9'>");
				} else if (now < last) {
					lines2.add("<td style='background:#f99'>");
				}
				last = now;
				lines2.add(m + " - " + t + "</td>");
			}
			lines.add("</tr>");
			lines2.add("</tr>");
		}
		lines.add("</table>");
		lines2.add("</table>");
		List<File> locations = EngineSourceManager.getLocations();
		if (!locations.isEmpty()) {
			Files.write(new File(locations.get(0), "nuwanislstats.html").toPath(), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			Files.write(new File(locations.get(0), "nuwanislstats2.html").toPath(), lines2, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		}
	}
}
