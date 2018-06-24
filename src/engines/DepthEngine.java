package engines;

import backend.Game.Data;
import backend.Player;

import static common.Constants.*;

public class DepthEngine implements Player
{
    private final int playerNumber;
    private final String name;
    private final byte[] depths;

    private Database db;

    public DepthEngine(int playerNumber, String name, byte[] depths)
    {
        this.playerNumber = playerNumber;
        this.name = name;
        this.depths = depths;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public int doMove(Data data)
    {
        final int move = data.getCurrentMove();
        if (move == 0) {
            return DEFENSE;
        }
        
        int dbres = db.findEntry(data, this.playerNumber);
        if (dbres != -1 && data.getElementsLeft(this.playerNumber, dbres) > 0) {
            return dbres;
        }
        
        int element = data.getMove(this.playerNumber ^ 1, move - 1);
        if (element == DEFENSE) {
            element = AIR; // see engine.vb:53
        }
        
        final int[] onedepth = new int[5];
        onedepth[WATER] = AIR;
        onedepth[FIRE] = WATER;
        onedepth[EARTH] = FIRE;
        onedepth[AIR] = EARTH;
        
        int i = (this.depths[move] + 4) % 4;
        while (i-- > 0 || data.getElementsLeft(this.playerNumber, element) == 0) {
            element = onedepth[element];
        }
        
        return element;
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
    }

    @Override
    public boolean isHumanControlled()
    {
        return false;
    }

    @Override
    public void useDatabase(Database db)
    {
        this.db = db;
    }
}
