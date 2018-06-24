package engines;

import backend.Game.Data;
import backend.Player;

public class FixedEngine implements Player
{
    private final int playerNumber;
    private final String name;
    private final int moves[];

    private Database db;

    public FixedEngine(int playerNumber, String name, int[] moves)
    {
        this.playerNumber = playerNumber;
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
        int dbres = db.findEntry(gamedata, this.playerNumber);
        if (dbres != -1) {
            return dbres;
        }
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
