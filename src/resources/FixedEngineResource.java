package resources;

import java.io.File;

import backend.Player;
import engines.FixedEngine;

import static common.Constants.*;

public class FixedEngineResource implements PlayerResource
{
    private final File resource;

    public FixedEngineResource(File resource)
    {
        this.resource = resource;
    }
    
    @Override
    public String getName()
    {
        return this.resource.getName();
    }

    @Override
    public String getPath()
    {
        return this.resource.getParentFile().getAbsolutePath();
    }

    @Override
    public int getTypeColor()
    {
        return 0xFFF494;
    }
    
    @Override
    public Player createPlayer(int playerNumber) throws Exception
    {
        final byte[] data = Resources.readFileCharacters(this.resource, 9);
        
        final byte[] elementsused = new byte[5];
        final int[] moves = new int[9];
        for (int i = 0; i < 9; i++) {
            int move = data[i] | 0x20;
            switch (move) {
            case 'w' : move = WATER; break;
            case 'v' : move = FIRE; break;
            case 'a' : move = EARTH; break;
            case 'l' : move = AIR; break;
            case 'd' : move = DEFENSE; break;
            default: throw new Exception("invalid element: '" + (char) move + "'");
            }
            elementsused[move]++;
        }
        
        if (elementsused[WATER] != 2 ||
            elementsused[FIRE] != 2 ||
            elementsused[EARTH] != 2 ||
            elementsused[AIR] != 2 ||
            elementsused[DEFENSE] != 1)
        {
            throw new Exception("invalid element sequence (is not 2 2 2 2 1)");
        }
        
        return new FixedEngine(this.resource.getName(), moves);
    }
}
