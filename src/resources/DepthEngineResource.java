package resources;

import java.io.File;

import backend.Player;
import engines.DepthEngine;

public class DepthEngineResource extends PlayerResource
{
    private final File resource;

    public DepthEngineResource(File resource)
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
    public Type getType()
    {
        return Type.DEPTH;
    }
    
    @Override
    public Player createPlayer(int playerNumber) throws Exception
    {
        final byte[] data = Resources.readFileCharacters(this.resource, 9);
        
        for (int i = 0; i < 9; i++) {
            if (data[i] < '0' || '9' < data[i]) {
                throw new Exception("invalid depth: " + (char) (data[i] & 0xFF));
            }
            data[i] -= '0';
        }
        
        return new DepthEngine(playerNumber, this.resource.getName(), data);
    }
}
