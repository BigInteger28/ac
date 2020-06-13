package engines;

import java.util.ArrayList;

import backend.Database;
import backend.Game.Data;

import static common.Constants.*;

public class Synergy2PRO extends DepthEngine
{
	private static final String NAME = "sYnergY 2 PRO";

	public static final Synergy2PRO INSTANCE = new Synergy2PRO();

	private static final Database db;

	static
	{
		int W = WATER, V = FIRE, A = EARTH, L = AIR, D = DEFENSE;

		ArrayList<Integer> dbEntries = new ArrayList<>();
		dbEntries.add(createEntry(W, D, A, V, A, L));
		dbEntries.add(createEntry(A, D, A, V, A, L, W));
		dbEntries.add(createEntry(V, W, D, W));
		dbEntries.add(createEntry(V, W, W, D, V, V));
		dbEntries.add(createEntry(A, W, A, V));
		dbEntries.add(createEntry(A, W, A, V, A));
		dbEntries.add(createEntry(L, W, A, V, D, L));
		dbEntries.add(createEntry(L, W, A, V, D, L, W));
		dbEntries.add(createEntry(W, W, A, V, D, L, W, L));
		dbEntries.add(createEntry(L, W, A, V, L, D));
		dbEntries.add(createEntry(V, W, A, V, L, D, W));
		dbEntries.add(createEntry(L, W, A, W, V));
		dbEntries.add(createEntry(A, W, A, W, V, D));
		dbEntries.add(createEntry(V, W, A, W, V, D, L));
		dbEntries.add(createEntry(V, W, L, W, D, V));
		dbEntries.add(createEntry(W, W, L, W, D, V, A));
		dbEntries.add(createEntry(L, W, L, W, D, V, A, V));
		dbEntries.add(createEntry(W, W, L, D));
		dbEntries.add(createEntry(A, W, L, D, V));
		dbEntries.add(createEntry(V, W, L, D, V, A, L));
		dbEntries.add(createEntry(L, W, L, D, V, A, L, A));
		dbEntries.add(createEntry(L, W, L, D, V, L, V));
		dbEntries.add(createEntry(W, W, L, V));
		dbEntries.add(createEntry(A, W, L, V, A, D, V));
		dbEntries.add(createEntry(A, V, L, W, A, D, A, W));
		dbEntries.add(createEntry(W, V, L, W, V, D, A));
		dbEntries.add(createEntry(L, V, L, W, V, D, A, A));
		dbEntries.add(createEntry(A, V, W, A, V, D, A));
		dbEntries.add(createEntry(W, V, W, L));
		dbEntries.add(createEntry(A, V, W, L, V));
		dbEntries.add(createEntry(L, V, W, L, V, L, D));
		dbEntries.add(createEntry(V, V, W, V, W));
		dbEntries.add(createEntry(L, A, V, A, L, D, V));
		dbEntries.add(createEntry(L, A, V, D));
		dbEntries.add(createEntry(A, A, V, D, W));
		dbEntries.add(createEntry(L, A, V, D, A));
		dbEntries.add(createEntry(L, L, A, L, A));
		dbEntries.add(createEntry(W, L, A, L, A, W));
		dbEntries.add(createEntry(V, L, A, V, W, D, L));
		dbEntries.add(createEntry(W, L, A, V, W, D, L, A));
		dbEntries.add(createEntry(V, L, A, W, D));
		dbEntries.add(createEntry(A, L, A, W, D, A));
		dbEntries.add(createEntry(W, L, A, W, D, A, W));
		dbEntries.add(createEntry(A, L, A, W, D, A, W, V));
		dbEntries.add(createEntry(A, L, A, W, V, W, D, V));
		dbEntries.add(createEntry(A, L, A, V, W, D));
		dbEntries.add(createEntry(A, L, V, A, W));
		dbEntries.add(createEntry(L, L, V, A, W, D));
		dbEntries.add(createEntry(V, L, V, A, W, D, W));
		dbEntries.add(createEntry(W, L, V, A, W, D, W, A));
		dbEntries.add(createEntry(W, L, W, A, L, D));
		dbEntries.add(createEntry(A, V, W, V, W, D));
		dbEntries.add(createEntry(A, V, W, V, W, D, L));
		dbEntries.add(createEntry(L, V, W, V, W, D, L, A));
		dbEntries.add(createEntry(V, L, A, W, D, A, W));
		dbEntries.add(createEntry(W, L, D, V, W, W));
		db = new Database(NAME + " db", dbEntries);
	}

	private static int createEntry(int result, int... moves)
	{
		int hash = 0x77777777;
		for (int i = 0; i < moves.length; i++) {
			hash = hash << 4 | (moves[i] & 0xF);
		}
		return (hash << 4) | (result & 0xF);
	}

	Synergy2PRO()
	{
		super(NAME, 8, 8, 9, 9, 9, 6, 7, 8, 0);
	}

	@Override
	public int doMove(int p, Database _unuseddb, Data data)
	{
		return super.doMove(p, db, data);
	}

	@Override
	public boolean canUseDatabase()
	{
		return false;
	}
}
