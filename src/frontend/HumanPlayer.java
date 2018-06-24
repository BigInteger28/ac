package frontend;

import backend.Game;
import backend.Player;
import engines.Database;

public class HumanPlayer implements Player
{
    private final String name;

    private int chosenElement;

    public HumanPlayer(String name)
    {
        this.name = name;
    }

    void setChosenElement(int element)
    {
        this.chosenElement = element;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public int doMove(int p, Game.Data data)
    {
        return this.chosenElement;
    }

    @Override
    public void onGameStart(Game.Data data, int yourPlayerNumber)
    {
        this.chosenElement = -1;
    }

    @Override
    public void onMoveDone(int yourMove, int otherMove, int score)
    {
        this.chosenElement = -1;
    }

    @Override
    public void onGameEnd(Game.Data data)
    {
    }

    @Override
    public boolean isHumanControlled()
    {
        return true;
    }

    @Override
    public void useDatabase(Database db)
    {
    }
}
