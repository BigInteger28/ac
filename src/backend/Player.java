package backend;

public interface Player
{
    String getName();
    int doMove(Game.Data gamedata);
    void onNewGame(String otherPlayerName);
    void onMoveDone(int yourMove, int otherMove, int score);
}
