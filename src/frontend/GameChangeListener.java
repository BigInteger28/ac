package frontend;

import backend.Game;

public interface GameChangeListener
{
    void onGameChanged(Game.Data data);
}
