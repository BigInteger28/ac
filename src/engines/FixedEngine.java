package engines;

import backend.Game.Data;
import backend.Player;

public class FixedEngine implements Player
{
    final String name;
    final int moves[];

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

}
