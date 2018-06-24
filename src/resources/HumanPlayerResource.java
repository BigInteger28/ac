package resources;

import backend.Player;
import frontend.HumanPlayer;

public class HumanPlayerResource implements PlayerResource
{
    @Override
    public String getName()
    {
        return "<Human player>";
    }
    
    @Override
    public Player createPlayer(int playerNumber)
    {
        return new HumanPlayer("Human " + playerNumber);
    }
}
