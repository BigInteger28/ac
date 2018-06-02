package backend;

public class Engine {
    private final int[] DEPTHS;
    private int currentmove;

    public Engine() {
        DEPTHS = new int[]{0, 17, 36, 12, 94, 28, 147, 12, 7}; //TODO: depths uit een file laden
        currentmove = 0;
    }

    public int getMove(int playermove) {
        int move = DEPTHS[currentmove] % 5;
        currentmove++;
        return move;
    }
}
