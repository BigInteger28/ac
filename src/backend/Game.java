package backend;

import static common.Constants.*;

public class Game
{
	private Listener listener;

	public int chosenElement[] = { -1, -1 };
	public Player p1, p2;
	public Database db1, db2;
	public Data data;

	public Game()
	{
		this.listener = new ListenerAdapter();
	}

	public Game(Listener listener)
	{
		this.listener = listener;
	}

	public void startNewGame()
	{
		this.data = new Data(this.p1, this.p2, this.db1, this.db2);
		this.chosenElement[0] = -1;
		this.chosenElement[1] = -1;
		this.listener.onGameStart(this);
		this.update();
	}

	public void undoMove()
	{
		this.chosenElement[0] = this.chosenElement[1] = -1;
		this.data.playerReady[0] = this.data.playerReady[1] = false;
		if (this.data.currentMove > 0) {
			this.data.currentMove--;
			this.data.elementsLeft[0][this.data.moves[0][this.data.currentMove]]++;
			this.data.elementsLeft[1][this.data.moves[1][this.data.currentMove]]++;
			switch (this.data.moveScores[this.data.currentMove]) {
			case -1: this.data.score[1]--; break;
			case 1: this.data.score[0]--; break;
			}
		}
		this.listener.onMoveDone(this);
		this.update();
	}

	public void update()
	{
		for (;;) {
			if (this.data.currentMove > 8) {
				return;
			}

			int e0 = this.askPlayerMove(0);
			int e1 = this.askPlayerMove(1);

			if (e0 == -1 || e1 == -1) {
				this.data.playerReady[0] = e0 != -1;
				this.data.playerReady[1] = e1 != -1;
				this.chosenElement[0] = e0;
				this.chosenElement[1] = e1;
				return;
			}

			this.data.elementsLeft[0][e0]--;
			this.data.elementsLeft[1][e1]--;
			final int result = RESULTMATRIX[e0][e1];
			final int c = this.data.currentMove++;
			this.data.moveScores[c] = result;
			this.data.moves[0][c] = e0;
			this.data.moves[1][c] = e1;
			final int[] dscore = { 0, 0, 1 };
			this.data.score[0] += dscore[result + 1];
			this.data.score[1] += dscore[result * -1 + 1];

			this.listener.onMoveDone(this);

			this.chosenElement[0] = -1;
			this.chosenElement[1] = -1;

			if (this.data.currentMove > 8) {
				this.data.players[0].onGameEnd(0, this.data.db[0], this.data);
				this.data.players[1].onGameEnd(1, this.data.db[1], this.data);
				return;
			}
		}
	}

	private int askPlayerMove(int p)
	{
		if (this.chosenElement[p] != -1) {
			return this.chosenElement[p];
		}

		int element = this.data.players[p].doMove(p, this.data.db[p], this.data);
		if (element == -1) {
			return -1;
		}

		for (int i = 0; i < 5; i++) {
			if (data.elementsLeft[p][element] != 0) {
				return element;
			}
			element = (element + 1) % 5;
		}

		return element;
	}

	public static class Data
	{
		private final Player[] players;
		private final Database[] db;
		private final int[][] moves;
		private final int[] moveScores;
		private int currentMove;
		private final int[][] elementsLeft = {
			{ 2, 2, 2, 2, 1 },
			{ 2, 2, 2, 2, 1 },
		};
		private final int[] score;
		private final boolean[] playerReady = { false, false };

		Data(Player p1, Player p2, Database db1, Database db2)
		{
			this.players = new Player[] { p1, p2 };
			this.db = new Database[] { db1, db2 };
			this.moves = new int[][] { new int[9], new int[9] };
			this.moveScores = new int[9];
			this.score = new int[] { 0, 0 };
		}

		public boolean isHumanControlled(int p)
		{
			return this.players[p] instanceof HumanControlled;
		}

		public String getPlayerName(int p)
		{
			return this.players[p].getName();
		}

		public int getMove(int p, int move)
		{
			return this.moves[p][move];
		}

		public int[] getMoves(int p)
		{
			int[] moves = new int[9];
			System.arraycopy(this.moves[p], 0, moves, 0, 9);
			return moves;
		}

		public int getMoveScore(int move)
		{
			return this.moveScores[move];
		}

		public int[] getElementsLeft(int p)
		{
			return new int[] {
				this.elementsLeft[p][0],
				this.elementsLeft[p][1],
				this.elementsLeft[p][2],
				this.elementsLeft[p][3],
				this.elementsLeft[p][4],
			};
		}

		public int getElementsLeft(int p, int element)
		{
			return this.elementsLeft[p][element];
		}

		public int getCurrentMove()
		{
			return this.currentMove;
		}

		public int getScore(int p)
		{
			return this.score[p];
		}

		public boolean isPlayerReady(int p)
		{
			return this.playerReady[p];
		}

		public boolean isFinished()
		{
			return this.currentMove > 8;
		}
	}

	public interface Listener
	{
		void onGameStart(Game game);
		void onMoveDone(Game game);
	}

	public static class ListenerAdapter implements Listener
	{
		@Override
		public void onGameStart(Game game)
		{
		}

		@Override
		public void onMoveDone(Game game)
		{
		}
	}
}
