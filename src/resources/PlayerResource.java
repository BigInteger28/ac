package resources;

import java.io.File;

import backend.Player;
import frontend.HumanPlayer;

public class PlayerResource
{
    private final File file;

    public PlayerResource(File file)
    {
        this.file = file;
    }
    
    public Player createPlayer(int playerNumber)
    {
        if (this.file == null) {
            return new HumanPlayer("Human " + playerNumber);
        }
        return null;

    }

}
