package frontend;

public interface FrontendController
{
    void startNewGame();
    void addGameChangeListener(GameChangeListener listener);
    void chooseElement(int player, int element);
}
