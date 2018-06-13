package backend;

public class Engine {
    private final int[] DEPTHS;

    public Engine(int[] depths) {
        DEPTHS = new int[9];
        for (int i = 0; i < depths.length; i++) {
            DEPTHS[i] = depths[i];
        }
    }

    public int getElement(int move, int playerelement) {
        if (move == 0) {
            return DEPTHS[0] % 5;
        }
        return (playerelement + DEPTHS[move]) % 5;
    }
}
