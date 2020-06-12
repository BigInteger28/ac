package engines.nuwanisl;

import backend.Game.Data;
import common.Constants;
import backend.Player;

public class NuwaniSL implements Player
{
	public static String NAME = "Nuwani SL";
	public static int loseScoreFactor = 5;

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
			// special case because the bitshift goes wrong (-1 >>> 32 results in -1?)
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
						System.out.printf("found enemy's pattern: (depths [");
						for (int j = 0; j < movesDone; j++) {
							if (j != 0) {
								System.out.printf(" ");
							}
							System.out.printf("%d", (variant.data[i * 2] >> (j * 4)) & 0x3);
						}
						System.out.printf("]");
						for (int j = movesDone; j < 7; j++) {
							System.out.printf(" %d", (variant.data[i * 2] >> (j * 4)) & 0x3);
						}
						System.out.printf("), this pattern was seen %d times%n", amountUsage);
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
		int nextDepthUsages[];
		int myMoveScores[];

		currentMove = gamedata.getCurrentMove();
		if (currentMove == 0) {
			return Constants.DEFENSE;
		}

		if (verbose) {
			System.out.printf("%ncurrent (0-based) move: %d%n", currentMove);
		}

		movesDone = currentMove - 1;
		myPlayedElements = gamedata.getMoves(this.myNumber);
		theirPlayedElements = gamedata.getMoves(this.theirNumber);
		value = this.value(myPlayedElements, theirPlayedElements, movesDone);

		myElementsLeft = gamedata.getElementsLeft(this.myNumber);
		myPreviousElement = myPlayedElements[currentMove - 1];

		nextDepthUsages = calculateNextDepthUsages(value, this.db, movesDone);
		myMoveScores = new int[4];
		for (int myNext = 0; myNext < 4; myNext++) {
			if (myElementsLeft[myNext] > 0) {
				if (verbose) {
					System.out.printf("- if I would play %c, then%n", Constants.CHARELEMENTS[myNext]);
				}
				for (int j = 0; j < 4; j++) {
					int theirNext = myPreviousElement + j;
					if (theirNext >= 4) {
						theirNext -= 4;
					}

					int score = Constants.RESULTMATRIX[myNext][theirNext];
					int moveScore;
					if (score > 0) {
						moveScore = nextDepthUsages[j];
					} else if (score < 0) {
						moveScore = -loseScoreFactor * nextDepthUsages[j];
					} else {
						moveScore = 0;
					}
					myMoveScores[myNext] += moveScore;

					if (verbose) {
						System.out.printf(
							"  they used depth %d (=%c) for %d times * score %d = %d%n",
							j,
							Constants.CHARELEMENTS[theirNext],
							nextDepthUsages[j],
							score,
							moveScore);
					}
				}
			} else {
				if (verbose) {
					System.out.printf("- I can't play %c, none left%n", Constants.CHARELEMENTS[myNext]);
				}
			}
		}

		if (verbose) {
			System.out.printf(
				"MY SCORE IF I PLAY ELEMENT: %c=%d %c=%d %c=%d %c=%d%n",
				Constants.CHARELEMENTS[0],
				myMoveScores[0],
				Constants.CHARELEMENTS[1],
				myMoveScores[1],
				Constants.CHARELEMENTS[2],
				myMoveScores[2],
				Constants.CHARELEMENTS[3],
				myMoveScores[3]
			);
		}

		int maxValue, maxValueIndex = 0;
		for (int j = 0; j < 4; j++) {
			maxValue = Integer.MIN_VALUE;
			for (int i = 0; i < 4; i++) {
				if (myMoveScores[i] > maxValue) {
					maxValue = myMoveScores[i];
					maxValueIndex = i;
				}
			}
			if (verbose) {
				System.out.printf("best score is move %c with score %d%n", Constants.CHARELEMENTS[maxValueIndex], maxValue);
			}
			if (myElementsLeft[maxValueIndex] > 0) {
				return maxValueIndex;
			}
			if (verbose) {
				System.out.println("I don't have this element");
			}
			myMoveScores[maxValueIndex] = Integer.MIN_VALUE;
		}

		return 0;
	}

	@Override
	public void onGameStart(Data gamedata, int yourPlayerNumber)
	{
		this.myNumber = yourPlayerNumber;
		this.theirNumber = yourPlayerNumber ^ 1;
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

		if (verbose) {
			int myPrev, theirCurrent;

			System.out.println();
			System.out.println("END ANALYSIS");
			for (int i = 1; i < 7; i++) {
				myPrev = myPlayedElements[i - 1];
				theirCurrent = theirPlayedElements[i];
				System.out.printf(
					"(0-based) move %d: my previous: %c their current: %c depth %d%n",
					i,
					Constants.CHARELEMENTS[myPrev],
					Constants.CHARELEMENTS[theirCurrent],
					this.depth(myPrev, theirCurrent)
				);
			}
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
}
