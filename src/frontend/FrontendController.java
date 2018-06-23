package frontend;

public interface FrontendController
{
    void startNewGameAdv();
    void startNewGame();
    void addGameChangeListener(GameChangeListener listener);
    void chooseElement(int player, int element);
}
