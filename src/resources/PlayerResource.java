package resources;

import backend.Player;

public abstract class PlayerResource
{
    public abstract String getName();
    public abstract String getPath();
    public abstract int getTypeColor();
    public abstract Player createPlayer(int playerNumber) throws Exception;

    @Override
    public String toString()
    {
        return this.getName();
    }
}
