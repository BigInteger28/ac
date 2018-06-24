package engines;

import backend.Game;
import backend.Player;

import static common.Constants.*;

public class DepthEngine implements Player
{
    private final String name;
    private final byte[] depths;

    private Database db;

    public DepthEngine(String name, byte[] depths)
    {
        this.name = name;
        this.depths = depths;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public int doMove(int p, Game.Data data)
    {
        final int move = data.getCurrentMove();
        if (move == 0) {
            return DEFENSE;
        }
        
        int dbres = db.findEntry(data, p);
        if (dbres != -1 && data.getElementsLeft(p, dbres) > 0) {
            return dbres;
        }
        
        int element = data.getMove(p ^ 1, move - 1);
        if (element == DEFENSE) {
            element = AIR; // see engine.vb:53
        }
        
        final int[] onedepth = new int[5];
        onedepth[WATER] = AIR;
        onedepth[FIRE] = WATER;
        onedepth[EARTH] = FIRE;
        onedepth[AIR] = EARTH;
        
        int i = (this.depths[move] + 4) % 4;
        while (i-- > 0 || data.getElementsLeft(p, element) == 0) {
            element = onedepth[element];
        }
        
        return element;
    }

    @Override
    public void onGameStart(Game.Data gamedata, int yourPlayerNumber)
    {
    }

    @Override
    public void onMoveDone(int yourMove, int otherMove, int score)
    {
    }

    @Override
    public void onGameEnd(Game.Data gamedata)
    {
    }

    @Override
    public boolean isHumanControlled()
    {
        return false;
    }
    
    @Override
    public boolean canUseDatabase()
    {
        return true;
    }

    @Override
    public void useDatabase(Database db)
    {
        this.db = db;
    }
}
