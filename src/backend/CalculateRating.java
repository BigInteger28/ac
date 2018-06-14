package backend;

import static java.lang.Math.round;

public class CalculateRating {

    private float sumTotRat;
    private float matches;
    private float score;
    private float lowestRatPlayed;
    private float highestRatPlayed;
    private float highestRatWon;
    private float lowestRatLose;


    public CalculateRating(float sumTotRat, float matches, float score, float lowestRatPlayed, float highestRatPlayed, float highestRatWon, float lowestRatLose) {
        this.sumTotRat = sumTotRat;
        this.matches = matches;
        this.score = score;
        this.lowestRatPlayed = lowestRatPlayed;
        this.highestRatPlayed = highestRatPlayed;
        this.highestRatWon = highestRatWon;
        this.lowestRatLose = lowestRatLose;
    }

    private float getPrestigeRating() {
        float rating = (((sumTotRat / matches) + ((highestRatWon + lowestRatLose) / 2)) / 2) + ((score - (matches / 2)) * ((highestRatPlayed - lowestRatPlayed) / matches));
        return round(rating);
    }
    //Je kan een round starten. En een round eindigen. Een round is een aantal engines waar je tegen speelt om dan uiteindelijk je prestige te berekenen.
    //Wanneer je tegen een engine speelt vraagt de front-end het aantal stars van die engine. Op het einde van de round wordt de formule getPrestigeRating opgeroepen

    private float getNewRating(float currentRating) {
        //Dit is nog niet correct. De negatieve waarden kloppen niet en de berekening is nog niet op punt
        float difference = getPrestigeRating() - currentRating;
        float maxIncrease = matches * 2;
        if (difference <= maxIncrease) {
            return currentRating + difference;
        } else {
            return currentRating + maxIncrease;
        }
    }
}
