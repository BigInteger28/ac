package engines;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import backend.Game.Data;
import common.Constants;
import frontend.Main;
import backend.Player;
import resources.PlayerResource;

public class NuwaniSL implements Player
{
	private static String NAME = "Nuwani SL";
	private static byte MARK_START_ENGINE_DATA = 8;
	private static byte MARK_START_PLAYER_DATA = 7;
	private static byte MARK_END_DATA = 6;

	private static File databaseFile;

	// int for depths:
	// 00 00 00 00
	// x7 65 43 21
	// set first bit for metadata
	// int for amount played
	// 4^7 = 16384
	private static int engineData[] = new int[16384 * 2];
	private static int playerData[] = new int[16384 * 2];
	private static int numEngineData, numPlayerData;

	static
	{
		databaseFile = new File(Main.settingsDir, "nuwani.sl");
		readDatabase();
		Runtime.getRuntime().addShutdownHook(new Thread(NuwaniSL::writeDatabase));
	}

	private static void readDatabase()
	{
		int data, count, a, b;

		numEngineData = numPlayerData = 0;
		if (!databaseFile.exists()) {
			return;
		}

		// TODO: make a backup?
		try (FileInputStream in = new FileInputStream(databaseFile)) {
			for (;;) {
				data = in.read();
				count = 0;
				a = b = 0;
				if (data == -1) {
					return;
				} else if (data == MARK_START_ENGINE_DATA) {
					for (;;) {
						data = in.read();
						if (data == -1) {
							return;
						} else if (count == 0 && data == MARK_END_DATA) {
							break;
						}
						if (count < 4) {
							a |= ((data & 0xFF) << (count * 8));
						} else if (count < 8) {
							b |= ((data & 0xFF) << (count * 8));
						}
						count++;
						if (count == 8) {
							engineData[numEngineData * 2] = a;
							engineData[numEngineData * 2 + 1] = b;
							numEngineData++;
							count = 0;
							a = b = 0;
						}
					}
				} else if (data == MARK_START_PLAYER_DATA) {
					for (;;) {
						data = in.read();
						if (data == -1) {
							return;
						} else if (count == 0 && data == MARK_END_DATA) {
							break;
						}
						if (count < 4) {
							a |= ((data & 0xFF) << (count * 8));
						} else if (count < 8) {
							b |= ((data & 0xFF) << (count * 8));
						}
						count++;
						if (count == 8) {
							playerData[numPlayerData * 2] = a;
							playerData[numPlayerData * 2 + 1] = b;
							numPlayerData++;
							count = 0;
							a = b = 0;
						}
					}
				} else {
					System.out.printf("wrong data in SL db: %02X%n", data);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void writeDatabase()
	{
		try (FileOutputStream out = new FileOutputStream(databaseFile)) {
			out.write(MARK_START_ENGINE_DATA);
			for (int i = 0; i < numEngineData; i++) {
				int a = engineData[i * 2];
				int b = engineData[i * 2 + 1];
				out.write((a) & 0xFF);
				out.write((a >>> 8) & 0xFF);
				out.write((a >>> 16) & 0xFF);
				out.write((a >>> 24) & 0xFF);
				out.write((b) & 0xFF);
				out.write((b >>> 8) & 0xFF);
				out.write((b >>> 16) & 0xFF);
				out.write((b >>> 24) & 0xFF);
			}
			out.write(MARK_END_DATA);
			out.write(MARK_START_PLAYER_DATA);
			for (int i = 0; i < numPlayerData; i++) {
				int a = playerData[i * 2];
				int b = playerData[i * 2 + 1];
				out.write((a) & 0xFF);
				out.write((a >>> 8) & 0xFF);
				out.write((a >>> 16) & 0xFF);
				out.write((a >>> 24) & 0xFF);
				out.write((b) & 0xFF);
				out.write((b >>> 8) & 0xFF);
				out.write((b >>> 16) & 0xFF);
				out.write((b >>> 24) & 0xFF);
			}
			out.write(MARK_END_DATA);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static int indexFor(int value, int in[], int count, int mask)
	{
		if (mask == -1) {
			for (int i = 0; i < count; i++) {
				if (in[i * 2] == value) {
					return i * 2;
				}
			}
		} else {
			for (int i = 0; i < count; i++) {
				if ((in[i * 2] & mask) == value) {
					return i * 2;
				}
			}
		}
		return -1;
	}

	/**
	 * @return int array with first element depth that is most used at this point, second value
	 * is depth that is second most used, ..
	 */
	private static int[] calculateFor(int value, int data[], int numdata, int zet)
	{
		int counts[] = { 0, 0, 0, 0 };
		int mask;

		if (zet - 1 == 0) {
			// otherwise the bitshift goes wrong (-1 >>> 32 = -1???)
			for (int i = 0; i < numdata; i++) {
				counts[data[i * 2] & 0x3] += data[i * 2 + 1];
			}
		} else {
			mask = -1 >>> (32 - (zet - 1) * 4);
			for (int i = 0; i < numdata; i++) {
				if ((data[i * 2]  & mask) == value) {
					counts[(data[i * 2] >> ((zet - 1) * 4)) & 0x3] += data[i * 2 + 1];
				}
			}
		}
		// fix: check what loses the most and subtract
		int depths[] = new int[4];
		int maxv, maxi;
		for (int j = 0; j < 4; j++) {
			maxv = -1;
			maxi = 0;
			for (int i = 0; i < 4; i++) {
				if (counts[i] > maxv) {
					maxv = counts[i];
					maxi = i;
				}
			}
			depths[j] = maxi;
			counts[maxi] = -1;
		}
		return depths;
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
		int zet = gamedata.getCurrentMove();
		int value;
		boolean human;
		int options[];

		if (zet == 0) {
			return Constants.DEFENSE;
		}

		human = gamedata.isHumanControlled(this.myPlayer ^ 1);
		value = this.value(gamedata.getMoves(this.myPlayer), gamedata.getMoves(this.myPlayer ^ 1), zet - 1);
		int data[];
		int numdata;
		if (human) {
			data = playerData;
			numdata = numPlayerData;
		} else {
			data = engineData;
			numdata = numEngineData;
		}
		options = calculateFor(value, data, numdata, zet);
		int myLastZet = gamedata.getMove(this.myPlayer, zet - 1);
		for (int i = 0; i < options.length; i++) {
			int option = myLastZet + options[i];
			option += 3; // counter zet
			while (option >= 4) {
				option -= 4;
			}
			if (gamedata.getElementsLeft(this.myPlayer, option) > 0) {
				return option;
			}
		}

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

		int index;
		int data[];
		if (human) {
			data = playerData;
			index = NuwaniSL.indexFor(value, data, numPlayerData, -1);
		} else {
			data = engineData;
			index = NuwaniSL.indexFor(value, data, numEngineData, -1);
		}

		if (index != -1) {
			data[index + 1]++;
		} else {
			if (human) {
				index = numPlayerData * 2;
				numPlayerData++;
			} else {
				index = numEngineData * 2;
				numEngineData++;
			}
			data[index] = value;
			data[index + 1] = 1;
		}
	}

	private int value(int myMoves[], int otherMoves[], int zetten)
	{
		int value = 0;
		for (int i = 0; i < zetten; i++) {
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

	public static class Resource extends PlayerResource
	{
		@Override
		public Player createPlayer(int playerNumber) throws Exception
		{
			return new NuwaniSL(playerNumber);
		}

		@Override
		public String getName()
		{
			return '<' + NAME + '>';
		}

		@Override
		public String getPath()
		{
			return null;
		}

		@Override
		public int getType()
		{
			return PlayerResource.TYPE_BUILTIN;
		}
	}
}
