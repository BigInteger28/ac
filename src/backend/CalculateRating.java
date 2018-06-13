package backend;

public class CalculateRating {

    private final String[] BELTS = {"White", "Grey", "Yellow", "Orange", "Lime", "Green", "Turquoise", "Blue", "Dark Blue", "Magenta", "Purple", "Brown", "Red", "Black"};


    private int first;
    private int second;
    private boolean isFirstHighest;

    public CalculateRating(int firstTotalStars, int secondTotalStars) {
        first = firstTotalStars;
        second = secondTotalStars;
        if (first > second) {
            isFirstHighest = true;
        } else {
            isFirstHighest = false;
        }
    }

    private void calculateRatings() {


    }

    private void calculateHighest(int rating, int other) {

    }

    private void calculateLowest(int rating, int other) {

    }

    private void calculatePlacement() {

    }

    public int getFirst() {

    }

    public int getSecond() {

    }
}
