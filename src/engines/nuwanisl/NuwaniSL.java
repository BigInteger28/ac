package engines.nuwanisl;

import backend.Game.Data;
import common.Constants;
import engines.Database;
import backend.Player;

public class NuwaniSL implements Player
{
	public static String NAME = "Nuwani SL";

	private static boolean verbose = System.getProperty("nuwanisl.verbose") != null;

	public static void init()
	{
		DB.init();
	}

	/**
	 * @return int array with 4 components, index 0 representing depth 0 etc. Value is amount of
	 *         times that depth is used in the next move.
	 */
	private static int[] calculateNextDepthUsages(int value, DB.Variant variant, int movesDone)
	{
		int nextDepthUsages[] = { 0, 0, 0, 0 };
		int mask;
		int opponentNextDepth;

		if (movesDone == 0) {
			// special case bacuse the bitshift goes wrong (-1 >>> 32 results in -1?)
			for (int i = 0; i < variant.numData; i++) {
				opponentNextDepth = variant.data[i * 2] & 0x3;
				nextDepthUsages[opponentNextDepth] += variant.data[i * 2 + 1];
			}
		} else {
			// 0x0 for 1, 0xF for 2, 0xFF for 3, ...
			mask = -1 >>> (32 - movesDone * 4);
			for (int i = 0; i < variant.numData; i++) {
				if ((variant.data[i * 2]  & mask) == value) {
					opponentNextDepth = (variant.data[i * 2] >> (movesDone * 4)) & 0x3;
					nextDepthUsages[opponentNextDepth] += variant.data[i * 2 + 1];
				}
			}
		}

		return nextDepthUsages;
	}

	private static int[] calculateTheirBestDepthFromUsages(int nextDepthUsages[])
	{
		int usagesReducedByCounters[] = new int[4];
		int depthPreferences[] = new int[4];
		int maxUsage, maxUsageIndex;

		usagesReducedByCounters[0] = nextDepthUsages[0] - nextDepthUsages[1] - 5;
		usagesReducedByCounters[1] = nextDepthUsages[1] - nextDepthUsages[2] - 5;
		usagesReducedByCounters[2] = nextDepthUsages[2] - nextDepthUsages[3] - 5;
		usagesReducedByCounters[3] = nextDepthUsages[3] - nextDepthUsages[0] - 5;

		for (int j = 0; j < 4; j++) {
			maxUsage = -1;
			maxUsageIndex = 0;
			for (int i = 0; i < 4; i++) {
				if (usagesReducedByCounters[i] > maxUsage) {
					maxUsage = usagesReducedByCounters[i];
					maxUsageIndex = i;
				}
			}
			depthPreferences[j] = maxUsageIndex;
			usagesReducedByCounters[maxUsageIndex] = -1;
		}
		return depthPreferences;
	}

	private int myPlayer;

	public NuwaniSL(int myPlayer)
	{
		this.myPlayer = myPlayer;
	}

	@Override
	public String getName()
	{
		return NAME;
	}

	@Override
	public int doMove(int p, Data gamedata)
	{
		int currentMove, movesDone;
		int myPlayedElements[], theirPlayedElements[];
		int value;
		int myPreviousElement;
		int mostLikelyDepths[];
		DB.Variant db;

		currentMove = gamedata.getCurrentMove();
		if (currentMove == 0) {
			return Constants.DEFENSE;
		}

		movesDone = currentMove - 1;
		myPlayedElements = gamedata.getMoves(this.myPlayer);
		theirPlayedElements = gamedata.getMoves(this.myPlayer ^ 1);
		value = this.value(myPlayedElements, theirPlayedElements, movesDone);

		db = gamedata.isHumanControlled(this.myPlayer ^ 1) ? db = DB.forPlayers : DB.forEngines;
		mostLikelyDepths = calculateTheirBestDepthFromUsages(calculateNextDepthUsages(value, db, movesDone));

		myPreviousElement = myPlayedElements[currentMove - 1];

		int enemy[] = calculateNextDepthUsages(value, db, movesDone);
		int scores[] = new int[4];
		for (int i = 0; i < 4; i++) {
			if (gamedata.getElementsLeft(this.myPlayer, i) > 0) {
				int myNext = i;
				for (int j = 0; j < 4; j++) {
					int theirNext = myPreviousElement + j;
					if (theirNext < 0) {
						theirNext += 4;
					}
					if (theirNext > 4) theirNext -= 4;
					int score = Constants.RESULTMATRIX[myNext][theirNext];
					if (score > 0) {
						scores[i] += enemy[j];
					} else if (score < 0) {
						scores[i] -= 2 * enemy[j];
					}
				}
			} else {
				scores[i] = Integer.MIN_VALUE;
			}
		}

		int maxv, maxi = 0;
		for (int j = 0; j < 4; j++) {
			maxv = Integer.MIN_VALUE;
			for (int i = 0; i < 4; i++) {
				if (scores[i] > maxv) {
					maxv = scores[i];
					maxi = i;
				}
			}
			if (gamedata.getElementsLeft(this.myPlayer, maxi) > 0) {
				return maxi;
			}
			scores[maxi] = Integer.MIN_VALUE;
		}
		/*
		for (int i = 0; i < mostLikelyDepths.length; i++) {
			int option = myPreviousElement + mostLikelyDepths[i];
			option += 3; // counter
			while (option >= 4) {
				option -= 4;
			}
			if (gamedata.getElementsLeft(this.myPlayer, option) > 0) {
				return option;
			}
		}
		*/

		return 0;
	}

	@Override
	public void onGameStart(Data gamedata, int yourPlayerNumber)
	{
	}

	@Override
	public void onMoveDone(int yourMove, int otherMove, int score)
	{
	}

	@Override
	public void onGameEnd(Data gamedata)
	{
		int value;
		boolean human;

		human = gamedata.isHumanControlled(this.myPlayer ^ 1);
		value = this.value(gamedata.getMoves(this.myPlayer), gamedata.getMoves(this.myPlayer ^ 1), 7);

		DB.Variant db = human ? DB.forPlayers : DB.forEngines;
		int index = db.indexFor(value, -1);

		if (index != -1) {
			db.data[index + 1]++;
		} else {
			index = db.numData * 2;
			db.data[index] = value;
			db.data[index + 1] = 1;
			db.numData++;
		}
	}

	private int value(int myMoves[], int otherMoves[], int movesDone)
	{
		int value = 0;
		for (int i = 0; i < movesDone; i++) {
			value |= this.depth(myMoves[i], otherMoves[i + 1]) << (i * 4);
		}
		return value;
	}

	private int depth(int myPrevious, int other)
	{
		int d = other - myPrevious;
		while (d < 0) {
			d += 4;
		}
		while (d >= 4) {
			d -= 4;
		}
		return d;
	}

	@Override
	public boolean isHumanControlled()
	{
		return false;
	}

	@Override
	public boolean canUseDatabase()
	{
		return false;
	}

	@Override
	public void useDatabase(Database db)
	{
	}
}
