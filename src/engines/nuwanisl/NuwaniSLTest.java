package engines.nuwanisl;

import java.util.ArrayList;
import java.util.Iterator;

import backend.ACMain;
import backend.Game;
import backend.Player;
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
		EngineSourceManager.collectResources(playerList, dbList, false);
		Game.Listener listener = new Game.Listener() {
			@Override
			public void onGameStart()
			{
			}

			@Override
			public void onMoveDone(int[] playerElements, int result)
			{
			}

			@Override
			public void onGameEnd(Game game)
			{
			}
		};
		PlayerResource nuwaniResource = new NuwaniSLResource();
		wins = losses = ties = 0;
		for (Iterator<PlayerResource> r = playerList.iterator() ; r.hasNext();) {
			PlayerResource playerResource = r.next();
			try {
				DB.forEngines = originalDbEngines.copy();
				Game g = new Game(listener);
				g.startNewGame(new Player[] {
					nuwaniResource.createPlayer(0),
					EngineSourceManager.makePlayerTryFindDatabase(playerResource, dbList, 1)
				});
				g.update();

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
