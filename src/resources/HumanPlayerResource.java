package resources;

import backend.Player;
import frontend.HumanPlayer;

public class HumanPlayerResource extends PlayerResource
{
    @Override
    public String getName()
    {
        return "<Human player>";
    }

    @Override
    public String getPath()
    {
        return null;
    }

    @Override
    public int getType()
    {
        return TYPE_HUMAN;
    }
    
    @Override
    public Player createPlayer(int playerNumber)
    {
        return new HumanPlayer("Human " + (playerNumber + 1));
    }
}
