package engines.nuwanisl;

import java.util.ArrayList;
import java.util.Iterator;

import backend.ACMain;
import backend.Game;
import backend.Player;
import resources.DatabaseResource;
import resources.EngineSourceManager;
import resources.PlayerResource;

public class NuwaniSLTraining
{
	static int wins, losses, ties;

	public static void main(String[] args)
	{
		ACMain.main();

		DB.reset();
		ArrayList<PlayerResource> playerList = new ArrayList<>();
		ArrayList<DatabaseResource> dbList = new ArrayList<>();
		EngineSourceManager.collectResources(playerList, dbList, true);
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
				int myScore, otherScore;

				myScore = game.data.getScore(0);
				otherScore = game.data.getScore(1);

				if (myScore > otherScore) {
					wins++;
				} else if (myScore == otherScore) {
					ties++;
				} else {
					losses++;
				}
			}
		};
		PlayerResource nuwaniResource = new NuwaniSLResource();
		for (int i = 0; i < 100; i++) {
			wins = losses = ties = 0;
			for (Iterator<PlayerResource> r = playerList.iterator() ; r.hasNext();) {
				try {
					Game g = new Game(listener);
					g.startNewGame(new Player[] {
						nuwaniResource.createPlayer(0),
						r.next().createPlayer(1)
					});
					g.update();
				} catch (Exception e1) {
					r.remove();
				}
			}
			System.out.printf("win %d lose %d tie %d%n", wins, losses, ties);
		}
	}
}
