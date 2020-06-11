package engines.nuwanisl;

import backend.Game.Data;
import common.Constants;
import engines.Database;
import backend.Player;

public class NuwaniSL implements Player
{
	public static String NAME = "Nuwani SL";
	public static int loseScoreFactor = 2;

	private static boolean verbose = System.getProperty("nuwanisl.verbose") != null;

	public static void init()
	{
		String loseScoreFactorProp;

		loseScoreFactorProp = System.getProperty("nuwanisl.losescorefactor");
		if (loseScoreFactorProp != null) {
			loseScoreFactor = Integer.parseInt(loseScoreFactorProp);
		}

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
		int amountUsage;

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
					amountUsage = variant.data[i * 2 + 1];
					nextDepthUsages[opponentNextDepth] += amountUsage;
					if (verbose) {
						System.out.printf(
							"found pattern for move %d, usages: %d%n",
							movesDone + 1,
							amountUsage
						);
					}
				}
			}
		}

		if (verbose) {
			System.out.printf(
				"enemy next depth usages: 0x%d 1x%d 2x%d 3x%d%n",
				nextDepthUsages[0],
				nextDepthUsages[1],
				nextDepthUsages[2],
				nextDepthUsages[3]
			);
		}

		return nextDepthUsages;
	}

	private int myNumber;
	private int theirNumber;
	private DB.Variant db;

	public NuwaniSL(int myNumber)
	{
		this.myNumber = myNumber;
		this.theirNumber = myNumber ^ 1;
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
		int myElementsLeft[];
		int mostLikelyEnemyDepths[];
		int theirDepthScores[];

		currentMove = gamedata.getCurrentMove();
		if (currentMove == 0) {
			return Constants.DEFENSE;
		}

		movesDone = currentMove - 1;
		myPlayedElements = gamedata.getMoves(this.myNumber);
		theirPlayedElements = gamedata.getMoves(this.theirNumber);
		value = this.value(myPlayedElements, theirPlayedElements, movesDone);

		myElementsLeft = gamedata.getElementsLeft(this.myNumber);
		myPreviousElement = myPlayedElements[currentMove - 1];

		mostLikelyEnemyDepths = calculateNextDepthUsages(value, this.db, movesDone);
		theirDepthScores = new int[4];
		for (int i = 0; i < 4; i++) {
			if (myElementsLeft[i] > 0) {
				int myNext = i;
				for (int j = 0; j < 4; j++) {
					int theirNext = myPreviousElement + j;
					if (theirNext >= 4) {
						theirNext -= 4;
					}
					int score = Constants.RESULTMATRIX[myNext][theirNext];
					if (score > 0) {
						theirDepthScores[i] -= mostLikelyEnemyDepths[j];
					} else if (score < 0) {
						theirDepthScores[i] += loseScoreFactor * mostLikelyEnemyDepths[j];
					}
				}
			} else {
				theirDepthScores[i] = Integer.MAX_VALUE;
			}
		}

		if (verbose) {
			System.out.printf(
				"enemy next depth scores: 0=%d 1=%d 2=%d 3=%d%n",
				theirDepthScores[0],
				theirDepthScores[1],
				theirDepthScores[2],
				theirDepthScores[3]
			);
		}

		int maxValue, maxValueIndex = 0;
		for (int j = 0; j < 4; j++) {
			maxValue = Integer.MAX_VALUE;
			for (int i = 0; i < 4; i++) {
				if (theirDepthScores[i] < maxValue) {
					maxValue = theirDepthScores[i];
					maxValueIndex = i;
				}
			}
			if (myElementsLeft[maxValueIndex] > 0) {
				return maxValueIndex;
			}
			theirDepthScores[maxValueIndex] = Integer.MAX_VALUE;
		}

		return 0;
	}

	@Override
	public void onGameStart(Data gamedata, int yourPlayerNumber)
	{
		if (gamedata.isHumanControlled(this.theirNumber)) {
			this.db = DB.forPlayers;
		} else {
			this.db = DB.forEngines;
		}
	}

	@Override
	public void onMoveDone(int yourMove, int otherMove, int score)
	{
	}

	@Override
	public void onGameEnd(Data gamedata)
	{
		int myPlayedElements[], theirPlayedElements[];
		int value, index;

		myPlayedElements = gamedata.getMoves(this.myNumber);
		theirPlayedElements = gamedata.getMoves(this.theirNumber);
		value = this.value(myPlayedElements, theirPlayedElements, 7);
		index = this.db.indexFor(value, -1);
		if (index != -1) {
			this.db.data[index + 1]++;
		} else {
			index = this.db.numData * 2;
			this.db.data[index] = value;
			this.db.data[index + 1] = 1;
			this.db.numData++;
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
