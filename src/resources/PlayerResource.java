package resources;

import backend.Player;

public interface PlayerResource
{
    String getName();
    String getPath();
    int getTypeColor();
    Player createPlayer(int playerNumber) throws Exception;
}
