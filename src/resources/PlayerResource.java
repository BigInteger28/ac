package resources;

import java.io.File;

import backend.Player;
import frontend.HumanPlayer;

public class PlayerResource
{
    public static final int HUMAN = 0;
    public static final int SAVED = 1;
    public static final int ENGINE = 2;

    private final int type;
    private final File file;
    
    private final String name;

    public PlayerResource(File file, int type)
    {
        this.file = file;
        this.type = type;
        
        if (file == null) {
            this.name = "<Human player>";
            return;
        }
        
        this.name = file.getName();
    }
    
    public String getName()
    {
        return this.name;
    }
    
    public Player createPlayer(int playerNumber)
    {
        if (this.file == null) {
            return new HumanPlayer("Human " + playerNumber);
        }
        return null;

    }

}
