package backend;

import engines.Database;

public interface Player
{
    String getName();
    int doMove(Game.Data gamedata);
    void onGameStart(Game.Data gamedata, int yourPlayerNumber);
    void onMoveDone(int yourMove, int otherMove, int score);
    void onGameEnd(Game.Data gamedata);
    boolean isHumanControlled();
    void useDatabase(Database db);
}
