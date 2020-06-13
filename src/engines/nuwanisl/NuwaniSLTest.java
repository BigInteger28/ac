package engines.nuwanisl;

import java.util.ArrayList;
import java.util.Iterator;

import backend.ACMain;
import backend.Database;
import backend.Game;
import backend.Player;
import resources.EngineSourceManager;

public class NuwaniSLTest
{
	static int wins, losses, ties;

	public static void main(String[] args) throws Exception
	{
		DB.Variant originalDbEngines;

		ACMain.main();

		originalDbEngines = DB.forEngines;
		DB.forEngines = new DB.Variant();

		ArrayList<Player> playerList = new ArrayList<>();
		ArrayList<Database> databases = new ArrayList<>();
		EngineSourceManager.collectResources(playerList, databases);
		wins = losses = ties = 0;
		Game g = new Game();
		for (Iterator<Player> r = playerList.iterator() ; r.hasNext();) {
			Player player = r.next();
			try {
				player.load();
				DB.forEngines = originalDbEngines.copy();
				g.p1 = NuwaniSL.INSTANCE;
				g.p2 = player;
				g.db2 = EngineSourceManager.tryFindDatabaseFor(player, databases);
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
				System.err.println(player.getPath() + '/' + player.getName());
				e1.printStackTrace();
			}
		}
		System.out.printf("%nwins %d losses %d ties %d%n", wins, losses, ties);

		DB.forEngines = originalDbEngines;
	}
}
