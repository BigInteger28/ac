package engines;

import backend.Game.Data;
import backend.Player;

public class FixedEngine implements Player
{
    private final String name;
    private final int moves[];
    
    private Database db;

    public FixedEngine(String name, int[] moves)
    {
        this.name = name;
        this.moves = moves;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public int doMove(Data gamedata)
    {
        return this.moves[gamedata.getCurrentMove()];
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
