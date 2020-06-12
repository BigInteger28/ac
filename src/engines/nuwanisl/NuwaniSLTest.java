package engines.nuwanisl;

import java.util.ArrayList;
import java.util.Iterator;

import backend.ACMain;
import backend.Game;
import resources.DatabaseResource;
import resources.EngineSourceManager;
import resources.PlayerResource;

public class NuwaniSLTest
{
	static int wins, losses, ties;

	public static void main(String[] args) throws Exception
	{
		DB.Variant originalDbEngines;

		ACMain.main();

		originalDbEngines = DB.forEngines;
		DB.forEngines = new DB.Variant();

		ArrayList<PlayerResource> playerList = new ArrayList<>();
		ArrayList<DatabaseResource> dbList = new ArrayList<>();
		EngineSourceManager.collectResources(playerList, dbList);
		PlayerResource nuwaniResource = new NuwaniSLResource();
		wins = losses = ties = 0;
		Game g = new Game();
		for (Iterator<PlayerResource> r = playerList.iterator() ; r.hasNext();) {
			PlayerResource playerResource = r.next();
			try {
				DB.forEngines = originalDbEngines.copy();
				g.p1 = nuwaniResource.createPlayer(0);
				g.p2 = EngineSourceManager.makePlayerTryFindDatabase(playerResource, dbList, 1);
				g.startNewGame();

				int m = g.data.getScore(0);
				int t = g.data.getScore(1);

				System.out.printf("%s - %s: %d-%d ", g.data.getPlayerName(0), g.data.getPlayerName(1), m, t);
				if (m > t) {
					System.out.println("win");
					wins++;
				} else if (m < t) {
					System.out.println("lose");
					losses++;
				} else {
					System.out.println("tie");
					ties++;
				}
			} catch (Exception e1) {
				r.remove();
				System.err.println(playerResource.getPath() + '/' + playerResource.getName());
				e1.printStackTrace();
			}
		}
		System.out.printf("%nwins %d losses %d ties %d%n", wins, losses, ties);

		DB.forEngines = originalDbEngines;
	}
}
