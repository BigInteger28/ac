package engines;

import java.util.ArrayList;
import java.util.List;

import resources.PlayerResource;
import resources.SingletonPlayerResource;

import static common.Constants.*;

public class Synergy2PRO extends DepthEngine
{
	public static final String NAME;
	public static final PlayerResource RESOURCE;

	private static final  List<Integer> db;

	static
	{
		int W = WATER, V = FIRE, A = EARTH, L = AIR, D = DEFENSE;

		db = new ArrayList<>();
		db.add(createEntry(W, D, A, V, A, L));
		db.add(createEntry(A, D, A, V, A, L, W));
		db.add(createEntry(V, W, D, W));
		db.add(createEntry(V, W, W, D, V, V));
		db.add(createEntry(A, W, A, V));
		db.add(createEntry(A, W, A, V, A));
		db.add(createEntry(L, W, A, V, D, L));
		db.add(createEntry(L, W, A, V, D, L, W));
		db.add(createEntry(W, W, A, V, D, L, W, L));
		db.add(createEntry(L, W, A, V, L, D));
		db.add(createEntry(V, W, A, V, L, D, W));
		db.add(createEntry(L, W, A, W, V));
		db.add(createEntry(A, W, A, W, V, D));
		db.add(createEntry(V, W, A, W, V, D, L));
		db.add(createEntry(V, W, L, W, D, V));
		db.add(createEntry(W, W, L, W, D, V, A));
		db.add(createEntry(L, W, L, W, D, V, A, V));
		db.add(createEntry(W, W, L, D));
		db.add(createEntry(A, W, L, D, V));
		db.add(createEntry(V, W, L, D, V, A, L));
		db.add(createEntry(L, W, L, D, V, A, L, A));
		db.add(createEntry(L, W, L, D, V, L, V));
		db.add(createEntry(W, W, L, V));
		db.add(createEntry(A, W, L, V, A, D, V));
		db.add(createEntry(A, V, L, W, A, D, A, W));
		db.add(createEntry(W, V, L, W, V, D, A));
		db.add(createEntry(L, V, L, W, V, D, A, A));
		db.add(createEntry(A, V, W, A, V, D, A));
		db.add(createEntry(W, V, W, L));
		db.add(createEntry(A, V, W, L, V));
		db.add(createEntry(L, V, W, L, V, L, D));
		db.add(createEntry(V, V, W, V, W));
		db.add(createEntry(L, A, V, A, L, D, V));
		db.add(createEntry(L, A, V, D));
		db.add(createEntry(A, A, V, D, W));
		db.add(createEntry(L, A, V, D, A));
		db.add(createEntry(L, L, A, L, A));
		db.add(createEntry(W, L, A, L, A, W));
		db.add(createEntry(V, L, A, V, W, D, L));
		db.add(createEntry(W, L, A, V, W, D, L, A));
		db.add(createEntry(V, L, A, W, D));
		db.add(createEntry(A, L, A, W, D, A));
		db.add(createEntry(W, L, A, W, D, A, W));
		db.add(createEntry(A, L, A, W, D, A, W, V));
		db.add(createEntry(A, L, A, W, V, W, D, V));
		db.add(createEntry(A, L, A, V, W, D));
		db.add(createEntry(A, L, V, A, W));
		db.add(createEntry(L, L, V, A, W, D));
		db.add(createEntry(V, L, V, A, W, D, W));
		db.add(createEntry(W, L, V, A, W, D, W, A));
		db.add(createEntry(W, L, W, A, L, D));
		db.add(createEntry(A, V, W, V, W, D));
		db.add(createEntry(A, V, W, V, W, D, L));
		db.add(createEntry(L, V, W, V, W, D, L, A));
		db.add(createEntry(V, L, A, W, D, A, W));
		db.add(createEntry(W, L, D, V, W, W));

		NAME = "sYnergY 2 PRO";
		RESOURCE = new SingletonPlayerResource(new Synergy2PRO());
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
		super(NAME, new byte[] { 8, 8, 9, 9, 9, 6, 7, 8, 0 });
		this.useDatabase(new Database(NAME + " db", db));
	}

	@Override
	public boolean canUseDatabase()
	{
		return false;
	}
}
