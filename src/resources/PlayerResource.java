package resources;

import backend.Player;

public interface PlayerResource
{
    String getName();
    Player createPlayer(int playerNumber) throws Exception;
}
