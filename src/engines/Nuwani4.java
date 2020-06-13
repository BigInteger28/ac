package engines;

import backend.Game.Data;
import backend.Player;
import backend.ResourceType;

import static common.Constants.*;

public class Nuwani4 implements Player
{
	public static final Nuwani4 INSTANCE = new Nuwani4();

	Nuwani4()
	{
	}

	@Override
	public ResourceType getType()
	{
		return ResourceType.BUILTIN;
	}

	@Override
	public String getPath()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "Nuwani 4";
	}

	@Override
	public int doMove(int p, Data data)
	{
		// ported from Nuwani4.vb
		int myMove, theirMove;
		int myQuantities[], theirQuantities[];
		boolean safe;
		boolean offensive;

		safe = true;
		offensive = true;

		if (data.getCurrentMove() == 0) {
			return DEFENSE;
		}

		myQuantities = data.getElementsLeft(p);
		theirQuantities = data.getElementsLeft(p ^ 1);

		if (offensive) {
			for (int i = 0; i < 5; i++) {
				int m = i == 0 ? 4 : i - 1;
				if (theirQuantities[i] == 0 && myQuantities[m] > 0) {
					return m;
				}
			}
		}

		if (safe) {
			for (int i = 0; i < 5; i++) {
				int m = i == 4 ? 0 : i + 1;
				if (theirQuantities[i] == 0 && myQuantities[m] > 0) {
					return m;
				}
			}
		}

		return 0;
	}

	@Override
	public boolean canUseDatabase()
	{
		return false;
	}
}
