package backend;

import static common.Constants.*;

import java.util.Arrays;

public class Game
{
	private final Listener listener;

	private Data data;

	private int chosenElement[] = { -1, -1 };

	public Game(Listener listener)
	{
		this.listener = listener;
	}

	public void startNewGame(Player[] players)
	{
		this.data = new Data(new Player[] { players[0], players[1] });
		Arrays.fill(this.chosenElement, -1);
		this.listener.onGameStart();
		players[0].onGameStart(this.data, 0);
		players[1].onGameStart(this.data, 1);
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

			this.listener.onMoveDone(this.chosenElement, result);
			this.data.players[0].onMoveDone(e0, e1, result);
			this.data.players[1].onMoveDone(e1, e0, -result);

			Arrays.fill(this.chosenElement, -1);

			if (this.data.currentMove > 8) {
				this.listener.onGameEnd();
				this.data.players[0].onGameEnd(this.data);
				this.data.players[1].onGameEnd(this.data);
				return;
			}
		}
	}

	private int askPlayerMove(int p)
	{
		if (this.chosenElement[p] != -1) {
			return this.chosenElement[p];
		}

		int element = this.data.players[p].doMove(p, this.data);
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

	public Data getData()
	{
		return this.data;
	}

	public static class Data
	{
		private final Player[] players;
		private final int[][] moves;
		private final int[] moveScores;
		private int currentMove;
		private final int[][] elementsLeft;
		private final int[] score;

		Data(Player[] players)
		{
			this.players = players;
			this.moves = new int[][] { new int[9], new int[9] };
			this.moveScores = new int[9];
			this.elementsLeft = new int[][] { new int[5], new int[5] };
			Arrays.fill(this.elementsLeft[0], 2);
			Arrays.fill(this.elementsLeft[1], 2);
			this.elementsLeft[0][DEFENSE] = 1;
			this.elementsLeft[1][DEFENSE] = 1;
			this.score = new int[] { 0, 0 };
		}

		public boolean isHumanControlled(int p)
		{
			return this.players[p].isHumanControlled();
		}

		public String getPlayerName(int p)
		{
			return this.players[p].getName();
		}

		public int getMove(int p, int move)
		{
			return this.moves[p][move];
		}

		public int getMoveScore(int move)
		{
			return this.moveScores[move];
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

	}

	public interface Listener
	{
		void onGameStart();

		void onMoveDone(int[] playerElements, int result);

		void onGameEnd();
	}

}
