package frontend;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.zip.CRC32;

import javax.imageio.ImageIO;

import backend.Database;
import backend.Game;
import backend.Player;
import resources.EngineSourceManager;

public class PictureSavedStorage
{
	private static final int PNGSTARTLEN = /*hdr*/ 8 + /*len*/ 4 + /*type*/ 4 + /*data*/ 13 + /*crc*/ 4;
	private static final String
		KEY_GAME_MOVE = "ac.g.m",
		KEY_P1_NAME = "ac.p1.name",
		KEY_P2_NAME = "ac.p2.name",
		KEY_P1_PATH = "ac.p1.path",
		KEY_P2_PATH = "ac.p2.path",
		KEY_DB1_NAME = "ac.db1.name",
		KEY_DB2_NAME = "ac.db2.name",
		KEY_DB1_PATH = "ac.db1.path",
		KEY_DB2_PATH = "ac.db2.path",
		KEY_P1_MOVES = "ac.p1.moves",
		KEY_P2_MOVES = "ac.p2.moves",
		KEY_P1_SCORE = "ac.p1.score",
		KEY_P2_SCORE = "ac.p2.score";

	private static CRC32 crc = new CRC32();
	private static ByteArrayOutputStream arrayOutput = new ByteArrayOutputStream(50000);

	private static void tEXt(OutputStream os, String strKey, String strValue) throws Exception
	{
		int len;
		byte[] key;
		byte[] data;
		byte[] value;

		key = strKey.getBytes(StandardCharsets.US_ASCII);
		if (strValue == null) {
			value = new byte[0];
		} else {
			value = strValue.getBytes(StandardCharsets.UTF_8);
		}
		data = new byte[4 + key.length + 1 + value.length + 4];
		len = key.length + /*zero sep*/ 1 + /*data*/ value.length;
		data[0] = (byte) ((len >>> 24) & 0xFF);
		data[1] = (byte) ((len >>> 16) & 0xFF);
		data[2] = (byte) ((len >>> 8) & 0xFF);
		data[3] = (byte) ((len & 0xFF));
		data[4] = 't';
		data[5] = 'E';
		data[6] = 'X';
		data[7] = 't';
		System.arraycopy(key, 0, data, 8, key.length);
		data[8 + key.length] = 0;
		System.arraycopy(value, 0, data, 9 + key.length, value.length);
		crc.reset();
		crc.update(data, 4, /*chunktype*/ 4 + len);
		os.write(data, 0, /*len*/ 4 + /*chunktype*/ 4 + len);
		len = (int) (crc.getValue());
		os.write((len >>> 24) & 0xFF);
		os.write((len >>> 16) & 0xFF);
		os.write((len >>> 8) & 0xFF);
		os.write((len) & 0xFF);
	}

	public static void saveGameStateInPng(BufferedImage image, Game game, File file) throws Exception
	{
		byte[] pngdata;
		char[] moves;

		arrayOutput.reset();
		ImageIO.write(image, "png", arrayOutput);
		pngdata = arrayOutput.toByteArray();

		if (pngdata.length < PNGSTARTLEN) {
			throw new Exception("generated image is corrupted");
		}

		moves = new char[9];
		try (FileOutputStream os = new FileOutputStream(file, false)) {
			os.write(pngdata, 0, PNGSTARTLEN);
			tEXt(os, KEY_GAME_MOVE, String.valueOf(game.data.getCurrentMove()));
			tEXt(os, KEY_P1_NAME, game.p1.getName());
			tEXt(os, KEY_P2_NAME, game.p2.getName());
			tEXt(os, KEY_P1_PATH, game.p1.getPath());
			tEXt(os, KEY_P2_PATH, game.p2.getPath());
			if (game.db1 != null) {
				tEXt(os, KEY_DB1_NAME, game.db1.getName());
				tEXt(os, KEY_DB1_PATH, game.db1.getPath());
			}
			if (game.db2 != null) {
				tEXt(os, KEY_DB2_NAME, game.db2.getName());
				tEXt(os, KEY_DB2_PATH, game.db2.getPath());
			}
			for (int i = 0; i < game.data.getCurrentMove(); i++) {
				moves[i] = (char) game.data.getMove(0, i);
			}
			tEXt(os, KEY_P1_MOVES, new String(moves, 0, game.data.getCurrentMove()));
			for (int i = 0; i < game.data.getCurrentMove(); i++) {
				moves[i] = (char) game.data.getMove(1, i);
			}
			tEXt(os, KEY_P2_MOVES, new String(moves, 0, game.data.getCurrentMove()));
			tEXt(os, KEY_P1_SCORE, String.valueOf(game.data.getScore(0)));
			tEXt(os, KEY_P2_SCORE, String.valueOf(game.data.getScore(1)));
			os.write(pngdata, PNGSTARTLEN, pngdata.length - PNGSTARTLEN);
		}
	}

	public static String loadGameStateFromPng(Game game, File file) throws Exception
	{
		byte[] buf;
		int len;
		int numMoves;
		String key;
		String value;
		HashMap<String, String> data;
		String pname;
		int[] p1moves;
		int[] p2moves;
		char[] readMoves;
		String p1score;
		String p2score;
		StringBuilder sb;
		ArrayList<Player> players;
		ArrayList<Database> databases;
		String path;
		String dbname;

		data = new HashMap<>();
		buf = new byte[1000];
		try (FileInputStream is = new FileInputStream(file)) {
			if (is.read(buf, 0, PNGSTARTLEN) != PNGSTARTLEN) {
				throw new Exception("unexpected EOF");
			}
			while (is.available() > 0) {
				len = 0;
				if (is.read(buf, 0, 4) != 4) {
					throw new Exception("unexpected EOF");
				}
				len |= (buf[0] << 24);
				len |= (buf[1] << 16);
				len |= (buf[2] << 8);
				len |= buf[3];
				if (is.read(buf, 0, 4) != 4) {
					throw new Exception("unexpected EOF");
				}
				if (buf[0] != 't' || buf[1] != 'E' || buf[2] != 'X' || buf[3] != 't') {
					break;
				}
				if (is.read(buf, 0, len) != len) {
					throw new Exception("unexpected EOF");
				}
				for (int i = 0; i < len; i++) {
					if (buf[i] == 0) {
						key = new String(buf, 0, i, StandardCharsets.US_ASCII);
						value = new String(buf, i + 1, len - i - 1, StandardCharsets.UTF_8);
						data.put(key, value);
					}
				}
				if (is.read(buf, 0, 4) != 4) {
					throw new Exception("unexpected EOF");
				}
			}
		}

		if (data.size() == 0) {
			throw new Exception("No data!");
		}
		if (!data.containsKey(KEY_GAME_MOVE)) {
			throw new Exception("Amount of moves is not present!");
		}
		if (!data.containsKey(KEY_P1_NAME)) {
			throw new Exception("Player 1 name is not present!");
		}
		if (!data.containsKey(KEY_P2_NAME)) {
			throw new Exception("Player 2 name is not present!");
		}
		if (!data.containsKey(KEY_P1_MOVES)) {
			throw new Exception("Player 1 moves are not present!");
		}
		if (!data.containsKey(KEY_P2_MOVES)) {
			throw new Exception("Player 2 moves are not present!");
		}

		sb = new StringBuilder();

		numMoves = data.get(KEY_GAME_MOVE).charAt(0) - '0';
		if (numMoves < 0 || 9 < numMoves) {
			throw new Exception("Invalid amount of moves: " + numMoves);
		}

		p1moves = new int[9];
		p2moves = new int[9];
		readMoves = data.get(KEY_P1_MOVES).toCharArray();
		if (readMoves.length != numMoves) {
			throw new Exception("Player 1 has " + readMoves.length + " moves but game has " + numMoves);
		}
		for (int i = 0; i < 9; i++) {
			if (i < numMoves) {
				p1moves[i] = readMoves[i];
				if (readMoves[i] < 0 || 5 < readMoves[i]) {
					throw new Exception("Invalid move saved for player 1 move " + i + ": " + readMoves[i]);
				}
			} else {
				p1moves[i] = -1;
			}
		}

		readMoves = data.get(KEY_P2_MOVES).toCharArray();
		if (readMoves.length != numMoves) {
			throw new Exception("Player 2 has " + readMoves.length + " moves but game has " + numMoves);
		}
		for (int i = 0; i < 9; i++) {
			if (i < numMoves) {
				p2moves[i] = readMoves[i];
				if (readMoves[i] < 0 || 5 < readMoves[i]) {
					throw new Exception("Invalid move saved for player 2 move " + i + ": " + readMoves[i]);
				}
			} else {
				p2moves[i] = -1;
			}
		}

		players = new ArrayList<>();
		databases = new ArrayList<>();
		EngineSourceManager.collectResources(players, databases);

		pname = data.get(KEY_P1_NAME);
		if ("Human".equals(pname)) {
			game.p1 = HumanPlayer.INSTANCE;
		} else {
			path = data.get(KEY_P1_PATH);
			if (path != null && path.isEmpty()) {
				path = null;
			}
			game.p1 = null;
			for (Player player : players) {
				if (pname.equals(player.getName()) && Objects.equals(path, player.getPath())) {
					game.p1 = player;
					break;
				}
			}
			if (game.p1 == null) {
				throw new Exception("Player 1 " + path + "/" + pname + " is not in the location list");
			}

			game.db1 = null;
			dbname = data.get(KEY_DB1_NAME);
			if (dbname != null) {
				path = data.get(KEY_DB1_PATH);
				if (path != null && path.isEmpty()) {
					path = null;
				}
				for (Database database : databases) {
					if (dbname.equals(database.getName()) && Objects.equals(path, database.getPath())) {
						game.db1 = database;
						break;
					}
				}
				if (game.db1 == null) {
					throw new Exception("Database 1 " + path + "/" + dbname + " is not in the location list");
				}
			}
		}

		pname = data.get(KEY_P2_NAME);
		if ("Human".equals(pname)) {
			game.p2 = HumanPlayer.INSTANCE;
		} else {
			path = data.get(KEY_P2_PATH);
			if (path != null && path.isEmpty()) {
				path = null;
			}
			game.p2 = null;
			for (Player player : players) {
				if (pname.equals(player.getName()) && Objects.equals(path, player.getPath())) {
					game.p2 = player;
					break;
				}
			}
			if (game.p2 == null) {
				throw new Exception("Player 2 " + path + "/" + pname + " is not in the location list");
			}

			game.db2 = null;
			dbname = data.get(KEY_DB2_NAME);
			if (dbname != null) {
				path = data.get(KEY_DB2_PATH);
				if (path != null && path.isEmpty()) {
					path = null;
				}
				for (Database database : databases) {
					if (dbname.equals(database.getName()) && Objects.equals(path, database.getPath())) {
						game.db2 = database;
						break;
					}
				}
				if (game.db2 == null) {
					throw new Exception("Database 2 " + path + "/" + dbname + " is not in the location list");
				}
			}
		}

		game.p1.load();
		game.p2.load();
		if (game.db1 != null) {
			game.db1.load();
		}
		if (game.db2 != null) {
			game.db2.load();
		}
		game.startNewGame();
		for (int i = 0; i < 9; i++) {
			HumanPlayer.chosenElement[0] = p1moves[i];
			HumanPlayer.chosenElement[1] = p2moves[i];
			game.update();
		}

		if (game.data.isFinished()) {
			p1score = data.get(KEY_P1_SCORE);
			if (p1score != null && !p1score.equals(String.valueOf(game.data.getScore(0)))) {
				sb.append("Player 1 score deviates: saved game value is " + p1score + ", now it's " + game.data.getScore(0));
				sb.append("\n");
			}
			p2score = data.get(KEY_P2_SCORE);
			if (p2score != null && !p2score.equals(String.valueOf(game.data.getScore(1)))) {
				sb.append("Player 2 score deviates: saved game value is " + p2score + ", now it's " + game.data.getScore(1));
				sb.append("\n");
			}
		}

		return sb.toString();
	}
}
