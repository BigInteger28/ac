package backend;

import static java.lang.Math.round;

public class CalculateRating {

    public float getPrestigeRating(float sumtotrat, float matches, float score, float highestRatWon, float lowestRatLose, float highestRatPlayed, float lowestRatPlayed) {
        float rating = (((sumtotrat / matches) + ((highestRatWon + lowestRatLose) / 2)) / 2) + ((score - (matches / 2)) * ((highestRatPlayed - lowestRatPlayed) / matches));
        return round(rating);
    }


}
