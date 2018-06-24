package engines;

import backend.Game.Data;
import backend.Player;

import java.util.Arrays;

import static common.Constants.*;

public class DepthEngine implements Player
{
    private final int playerNumber;
    private final String name;
    private final byte[] depths;
    private final int[] elementsLeft;

    private int otherplayerelement;
    private Database db;

    public DepthEngine(int playerNumber, String name, byte[] depths)
    {
        this.playerNumber = playerNumber;
        this.name = name;
        this.depths = depths;
        this.elementsLeft = new int[5];
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public int doMove(Data gamedata)
    {
        final int move = gamedata.getCurrentMove();
        if (move == 0) {
            return DEFENSE;
        }
        
        int dbres = db.findEntry(gamedata, this.playerNumber);
        if (dbres != -1 && this.elementsLeft[dbres] > 0) {
            this.elementsLeft[dbres]--;
            return dbres;
        }
        
        int element = this.otherplayerelement;
        if (element == DEFENSE) {
            element = AIR; // see engine.vb:53
        }
        
        final int[] onedepth = new int[5];
        onedepth[WATER] = AIR;
        onedepth[FIRE] = WATER;
        onedepth[EARTH] = FIRE;
        onedepth[AIR] = EARTH;
        
        int i = (this.depths[move] + 4) % 4;
        while (i-- > 0 || this.elementsLeft[element] == 0) {
            element = onedepth[element];
        }
        this.elementsLeft[element]--;
        
        return element;
    }

    @Override
    public void onGameStart(Data gamedata, int yourPlayerNumber)
    {
        Arrays.fill(this.elementsLeft, 2);
        this.elementsLeft[DEFENSE] = 1;
    }

    @Override
    public void onMoveDone(int yourMove, int otherMove, int score)
    {
        this.otherplayerelement = otherMove;
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
